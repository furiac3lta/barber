package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.entity.Business;
import com.marcedev.barberapp.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessRepository businessRepository;

    @PostMapping
    public Business create(@RequestBody Business business) {
        return businessRepository.save(business);
    }

    @GetMapping("/{id}")
    public Business getById(@PathVariable Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbería no encontrada"));
    }
}
