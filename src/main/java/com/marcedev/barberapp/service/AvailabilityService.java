package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.AvailabilityDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    List<AvailabilityDTO> getAvailabilityByBusiness(Long businessId);

    void setAvailability(
            Long businessId,
            DayOfWeek dayOfWeek,
            String startTime,
            String endTime
    );

    // ✅ ESTE MÉTODO FALTABA
    List<String> getAvailableSlots(
            Long businessId,
            Long serviceId,
            LocalDate date
    );
}
