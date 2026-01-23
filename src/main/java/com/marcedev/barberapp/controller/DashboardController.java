package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AppointmentResponse;
import com.marcedev.barberapp.dto.DashboardSummaryResponse;
import com.marcedev.barberapp.service.AppointmentService;
import com.marcedev.barberapp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AppointmentService appointmentService;
    private final DashboardService dashboardService;
    @GetMapping("/today")
    public List<AppointmentResponse> today(
            @RequestParam Long businessId
    ) {
        return appointmentService.getByDate(
                businessId,
                LocalDate.now()
        );
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary(
            @RequestParam Long businessId,
            Authentication auth
    ) {
        return dashboardService.getSummary(businessId);
    }
}
