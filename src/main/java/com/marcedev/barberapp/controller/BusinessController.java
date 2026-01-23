package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.BusinessRequest;
import com.marcedev.barberapp.dto.BusinessResponse;
import com.marcedev.barberapp.dto.BusinessUpdateRequest;
import com.marcedev.barberapp.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public BusinessResponse create(@Valid @RequestBody BusinessRequest request) {
        return businessService.create(request);
    }

    @GetMapping("/{id}")
    public BusinessResponse getById(@PathVariable Long id) {
        return businessService.getById(id);
    }

    @GetMapping
    public List<BusinessResponse> list() {
        return businessService.list();
    }

    @PutMapping("/{id}")
    public BusinessResponse update(@PathVariable Long id, @Valid @RequestBody BusinessUpdateRequest request) {
        return businessService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public BusinessResponse softDelete(@PathVariable Long id) {
        return businessService.softDelete(id);
    }
}
