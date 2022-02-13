package com.example.redditspringangular.service;

import com.example.redditspringangular.dto.RegisterRequest;
import com.example.redditspringangular.model.NotificationEmail;
import com.example.redditspringangular.model.User;
import com.example.redditspringangular.model.VerificationToken;
import com.example.redditspringangular.repository.UserRepository;
import com.example.redditspringangular.repository.VerificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final MailService mailService;

    public void signup(RegisterRequest registerRequest){
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
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
}
