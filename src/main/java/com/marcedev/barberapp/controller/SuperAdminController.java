package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AdminUserResponse;
import com.marcedev.barberapp.dto.UpdateAdminRequest;
import com.marcedev.barberapp.dto.BusinessRequest;
import com.marcedev.barberapp.dto.BusinessResponse;
import com.marcedev.barberapp.dto.BusinessUpdateRequest;
import com.marcedev.barberapp.dto.CreateAdminRequest;
import com.marcedev.barberapp.entity.Business;
import com.marcedev.barberapp.entity.User;
import com.marcedev.barberapp.enum_.Role;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.repository.UserRepository;
import com.marcedev.barberapp.service.BusinessService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final BusinessService businessService;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/businesses")
    public List<BusinessResponse> listBusinesses() {
        return businessService.listAll();
    }

    @PostMapping("/businesses")
    public BusinessResponse createBusiness(@Valid @RequestBody BusinessRequest request) {
        return businessService.create(request);
    }

    @PutMapping("/businesses/{id}")
    public BusinessResponse updateBusiness(
            @PathVariable Long id,
            @Valid @RequestBody BusinessUpdateRequest request
    ) {
        return businessService.update(id, request);
    }

    @DeleteMapping("/businesses/{id}")
    public BusinessResponse deactivateBusiness(@PathVariable Long id) {
        return businessService.setActive(id, false);
    }

    @PutMapping("/businesses/{id}/activate")
    public BusinessResponse activateBusiness(@PathVariable Long id) {
        return businessService.setActive(id, true);
    }

    @GetMapping("/admins")
    public List<AdminUserResponse> listAdmins() {
        return userRepository.findWithBusinessByRole(Role.ADMIN).stream()
                .map(this::toAdminResponse)
                .toList();
    }


    @PutMapping("/admins/{id}")
    public AdminUserResponse updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminRequest request
    ) {
        User user = getAdminById(id);
        validatePhoneChange(id, request.phone());
        validateEmailChange(id, request.email());
        Business business = businessRepository.findById(request.businessId())
                .orElseThrow(() -> new EntityNotFoundException("Business no encontrado"));

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setBusiness(business);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User saved = userRepository.save(user);
        return toAdminResponseById(saved.getId());
    }

    @DeleteMapping("/admins/{id}")
    public AdminUserResponse deactivateAdmin(@PathVariable Long id) {
        User user = getAdminById(id);
        user.setActive(false);
        User saved = userRepository.save(user);
        return toAdminResponseById(saved.getId());
    }

    @PutMapping("/admins/{id}/activate")
    public AdminUserResponse activateAdmin(@PathVariable Long id) {
        User user = getAdminById(id);
        user.setActive(true);
        User saved = userRepository.save(user);
        return toAdminResponseById(saved.getId());
    }

    @PostMapping("/admins")
    public AdminUserResponse createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Business business = businessRepository.findById(request.businessId())
                .orElseThrow(() -> new EntityNotFoundException("Business no encontrado"));

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .business(business)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        return toAdminResponseById(saved.getId());
    }

    private AdminUserResponse toAdminResponseById(Long id) {
        User user = userRepository.findWithBusinessById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return toAdminResponse(user);
    }


    private User getAdminById(Long id) {
        User user = userRepository.findWithBusinessById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Usuario no es admin");
        }
        return user;
    }

    private void validatePhoneChange(Long userId, String phone) {
        userRepository.findByPhone(phone).ifPresent(existing -> {
            if (!existing.getId().equals(userId)) {
                throw new IllegalArgumentException("El teléfono ya está registrado");
            }
        });
    }

    private void validateEmailChange(Long userId, String email) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(userId)) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        });
    }

    private AdminUserResponse toAdminResponse(User user) {
        Long businessId = user.getBusiness() != null ? user.getBusiness().getId() : null;
        String businessName = user.getBusiness() != null ? user.getBusiness().getName() : null;
        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                businessId,
                businessName,
                user.getActive()
        );
    }
}
