package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.LoginRequest;
import com.marcedev.barberapp.dto.LoginResponse;
import com.marcedev.barberapp.dto.MeResponse;
import com.marcedev.barberapp.dto.RegisterRequest;
import com.marcedev.barberapp.security.JwtService;
import com.marcedev.barberapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);

        return authService.me(userId);
    }
    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

}
