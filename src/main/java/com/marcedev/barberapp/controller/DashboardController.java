package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AppointmentResponse;
import com.marcedev.barberapp.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AppointmentService appointmentService;

    @GetMapping("/today")
    public List<AppointmentResponse> today(
            @RequestParam Long businessId
    ) {
        return appointmentService.getByDate(
                businessId,
                LocalDate.now()
        );
    }
}
