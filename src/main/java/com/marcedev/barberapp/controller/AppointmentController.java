package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AppointmentResponse;
import com.marcedev.barberapp.dto.CreateAppointmentRequest;
import com.marcedev.barberapp.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public List<AppointmentResponse> byDate(
            @RequestParam Long businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getByDate(businessId, date);
    }

    @PostMapping
    public AppointmentResponse create(@Valid @RequestBody CreateAppointmentRequest req) {
        return appointmentService.create(req);
    }

    @PutMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id) {
        return appointmentService.cancel(id);
    }

    @PutMapping("/{id}/attend")
    public AppointmentResponse attend(@PathVariable Long id) {
        return appointmentService.attend(id);
    }

    // ✅ CALENDARIO DIARIO (ESTE TE FALTA)
    @GetMapping("/day")
    public List<AppointmentResponse> getByDay(
            @RequestParam Long businessId,
            @RequestParam LocalDate date
    ) {
        return appointmentService.getByDate(businessId, date);
    }

    @GetMapping("/week")
    public Map<DayOfWeek, List<AppointmentResponse>> getWeek(
            @RequestParam Long businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getWeek(businessId, date);
    }


}
