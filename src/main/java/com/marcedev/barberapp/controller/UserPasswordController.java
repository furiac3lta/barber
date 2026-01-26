package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.ActionResponse;
import com.marcedev.barberapp.dto.ChangePasswordRequest;
import com.marcedev.barberapp.dto.ResetPasswordRequest;
import com.marcedev.barberapp.entity.User;
import com.marcedev.barberapp.enum_.Role;
import com.marcedev.barberapp.repository.UserRepository;
import com.marcedev.barberapp.security.AuthUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/password")
@RequiredArgsConstructor
public class UserPasswordController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/change")
    public ActionResponse changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        AuthUser auth = getAuthUser();
        if (auth == null) {
            throw new IllegalArgumentException("No autenticado");
        }

        if (!req.newPassword().equals(req.confirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        User user = userRepository.findById(auth.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
        return new ActionResponse("Contraseña actualizada");
    }

    @PostMapping("/reset")
    public ActionResponse resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        AuthUser auth = getAuthUser();
        if (auth == null) {
            throw new IllegalArgumentException("No autenticado");
        }
        if (!req.newPassword().equals(req.confirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        User target = resolveTarget(req);
        Role requesterRole = Role.valueOf(auth.role());

        if (requesterRole == Role.SUPER_ADMIN) {
            target.setPassword(passwordEncoder.encode(req.newPassword()));
            userRepository.save(target);
            return new ActionResponse("Contraseña reseteada");
        }

        if (requesterRole != Role.ADMIN) {
            throw new IllegalArgumentException("No autorizado");
        }

        Long requesterBusinessId = auth.businessId();
        Long targetBusinessId = target.getBusiness() != null ? target.getBusiness().getId() : null;
        if (requesterBusinessId == null || targetBusinessId == null || !requesterBusinessId.equals(targetBusinessId)) {
            throw new IllegalArgumentException("No autorizado para este negocio");
        }

        if (target.getRole() != Role.BARBER && target.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("No autorizado para este usuario");
        }

        target.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(target);
        return new ActionResponse("Contraseña reseteada");
    }

    private User resolveTarget(ResetPasswordRequest req) {
        if (req.targetUserId() != null) {
            return userRepository.findById(req.targetUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        }
        if (req.targetEmail() != null && !req.targetEmail().isBlank()) {
            return userRepository.findByEmail(req.targetEmail())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        }
        if (req.targetPhone() != null && !req.targetPhone().isBlank()) {
            return userRepository.findByPhone(req.targetPhone())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        }
        throw new IllegalArgumentException("Debes indicar usuario");
    }

    private AuthUser getAuthUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthUser user) return user;
        return null;
    }
}
