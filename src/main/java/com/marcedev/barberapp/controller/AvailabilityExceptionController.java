// src/main/java/com/marcedev/barberapp/controller/AvailabilityExceptionController.java
package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.AvailabilityExceptionRequest;
import com.marcedev.barberapp.dto.AvailabilityExceptionResponse;
import com.marcedev.barberapp.service.AvailabilityExceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/availability-exceptions")
@RequiredArgsConstructor
public class AvailabilityExceptionController {

    private final AvailabilityExceptionService service;

    @PostMapping
    public AvailabilityExceptionResponse upsert(@Valid @RequestBody AvailabilityExceptionRequest req) {
        return service.upsert(req);
    }

    @GetMapping
    public AvailabilityExceptionResponse get(
            @RequestParam Long businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.get(businessId, date);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestParam Long businessId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        service.delete(businessId, date);
    }
}
