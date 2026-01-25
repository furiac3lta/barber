package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.*;
import com.marcedev.barberapp.entity.User;
import com.marcedev.barberapp.repository.UserRepository;
import com.marcedev.barberapp.security.JwtService;
import com.marcedev.barberapp.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.marcedev.barberapp.enum_.Role;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.repository.BarberRepository;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BusinessRepository businessRepository;
    private final BarberRepository barberRepository;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (user.getActive() != null && !user.getActive()) {
            throw new IllegalArgumentException("Usuario desactivado");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        Long businessId = user.getBusiness() != null ? user.getBusiness().getId() : null;

        Long barberId = null;
        if (user.getRole() == Role.BARBER) {
            barberId = barberRepository.findByUserId(user.getId())
                    .map(com.marcedev.barberapp.entity.Barber::getId)
                    .orElse(null);
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getRole().name(),
                businessId,
                barberId
        );

        return new LoginResponse(
                token,
                user.getId(),
                businessId,
                user.getRole().name(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );

    }

    @Override
    public MeResponse me(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Long businessId = user.getBusiness() != null ? user.getBusiness().getId() : null;

        return new MeResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                businessId
        );
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        var business = businessRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Business no encontrado"));

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CLIENT)
                .business(business)
                .active(true)
                .build();

        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getRole().name(), user.getBusiness().getId());

        return new LoginResponse(
                token,
                user.getId(),
                user.getBusiness().getId(),
                user.getRole().name(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

}
