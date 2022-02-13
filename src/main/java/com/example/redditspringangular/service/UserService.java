package com.example.redditspringangular.service;

import com.example.redditspringangular.dto.AuthenticationResponse;
import com.example.redditspringangular.dto.LoginRequest;
import com.example.redditspringangular.dto.RefreshTokenRequest;
import com.example.redditspringangular.dto.RegisterRequest;
import com.example.redditspringangular.exception.SpringRedditException;
import com.example.redditspringangular.model.*;
import com.example.redditspringangular.repository.RoleRepository;
import com.example.redditspringangular.repository.UserRepository;
import com.example.redditspringangular.repository.VerificationRepository;
import com.example.redditspringangular.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;

    public void signup(RegisterRequest registerRequest){
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
                .orElseThrow(()-> new RuntimeException("ROLE USER IS NOT FOUND"));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("" +
                "Активируйте свой аккаунт",
                user.getEmail()
                , "Спасибо за регистрацию на нашем сайте, пожалуйста перейдите по ссылке чтобы  активировать аккаунт: http://localhost:8080/api/auth/accountVerification/" + token));

    }
    public String generateVerificationToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationRepository.findByToken(token).orElseThrow(()->new SpringRedditException("Invalid token"));
        fetchUserAndEnable(verificationToken);
    }

    public void fetchUserAndEnable(VerificationToken verificationToken){
        @NotBlank(message = "username is requered") String username = verificationToken.getUser().getUserName();
        User user = userRepository.findByUserName(username).orElseThrow(()->new SpringRedditException("User not found exception"));
        user.setEnabled(true);
        userRepository.save(user);
    }


    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
}
