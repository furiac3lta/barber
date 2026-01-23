package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.CreateServiceRequest;
import com.marcedev.barberapp.dto.ServiceResponse;
import com.marcedev.barberapp.dto.UpdateServiceRequest;
import com.marcedev.barberapp.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ServiceResponse create(@RequestBody CreateServiceRequest request) {
        return serviceService.create(request);
    }

   /* @GetMapping
    public List<ServiceResponse> getByBusiness(@RequestParam Long businessId) {
        return serviceService.getByBusiness(businessId);
    }*/
    // 3️⃣ Desactivar servicio (soft delete)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        serviceService.deactivate(id);
    }
    @GetMapping
    public List<ServiceResponse> list(
            @RequestParam Long businessId
    ) {
        return serviceService.list(businessId);
    }

    @PutMapping("/{id}")
    public ServiceResponse update(
            @PathVariable Long id,
            @RequestBody UpdateServiceRequest request
    ) {
        return serviceService.update(id, request);
    }


}