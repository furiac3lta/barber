package com.marcedev.barberapp.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BarberServicesRequest(
        @NotNull List<Long> serviceIds
) {}
