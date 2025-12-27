package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.BarberResponse;
import com.marcedev.barberapp.entity.Barber;
import com.marcedev.barberapp.repository.BarberRepository;
import com.marcedev.barberapp.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbers")
@RequiredArgsConstructor
public class BarberController {

    private final BarberRepository barberRepository;
    private final BusinessRepository businessRepository;

    @GetMapping
    public List<BarberResponse> getByBusiness(
            @RequestParam Long businessId
    ) {
        return barberRepository
                .findByBusinessIdAndActiveTrue(businessId)
                .stream()
                .map(BarberResponse::from)
                .toList();
    }

    @PostMapping
    public BarberResponse create(
            @RequestParam Long businessId,
            @RequestParam String name
    ) {
        var business = businessRepository.findById(businessId)
                .orElseThrow();

        Barber barber = Barber.builder()
                .name(name)
                .business(business)
                .active(true)
                .build();

        return BarberResponse.from(barberRepository.save(barber));
    }
}
