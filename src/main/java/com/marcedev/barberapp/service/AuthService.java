package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.LoginRequest;
import com.marcedev.barberapp.dto.LoginResponse;
import com.marcedev.barberapp.entity.User;
import com.marcedev.barberapp.enum_.Role;
import com.marcedev.barberapp.repository.UserRepository;
import com.marcedev.barberapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest req) {

        User user = userRepository.findByPhone(req.phone())
                .orElseThrow(() ->
                        new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("No autorizado");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getBusiness().getId(),
                user.getRole().name()
        );
    }
}
