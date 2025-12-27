// ===============================
// AVAILABILITY CONTROLLER (CORREGIDO)
// ===============================
package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AvailabilityRequest;
import com.marcedev.barberapp.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/slots")
    public List<String> getSlots(
            @RequestParam Long businessId,
            @RequestParam Long barberId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return availabilityService.getAvailableSlots(
                businessId, barberId, serviceId, date
        );
    }

    @PostMapping
    public void setAvailability(@RequestBody AvailabilityRequest req) {
        availabilityService.setAvailability(
                req.businessId(),
                req.dayOfWeek(),
                req.startTime(),
                req.endTime()
        );
    }

    @GetMapping("/{businessId}")
    public Object getAvailability(@PathVariable Long businessId) {
        return availabilityService.getAvailabilityByBusiness(businessId);
    }
}
