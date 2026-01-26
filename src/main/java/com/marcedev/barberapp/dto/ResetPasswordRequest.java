package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        Long targetUserId,
        String targetEmail,
        String targetPhone,
        @NotBlank String newPassword,
        @NotBlank String confirmPassword
) {}
