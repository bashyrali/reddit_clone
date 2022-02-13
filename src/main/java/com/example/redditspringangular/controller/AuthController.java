package com.example.redditspringangular.controller;

import com.example.redditspringangular.dto.RegisterRequest;
import com.example.redditspringangular.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        userService.signup(registerRequest);
        return new ResponseEntity<>("Пользователь успешно зарегистрирован", HttpStatus.OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<?> accountVerification(@PathVariable String token){

        return null ;
    }
}
