package com.marcedev.barberapp.controller;

import com.marcedev.barberapp.dto.BarberResponse;
import com.marcedev.barberapp.dto.BarberServicesRequest;
import com.marcedev.barberapp.dto.BarberUpdateRequest;
import com.marcedev.barberapp.entity.Barber;
import com.marcedev.barberapp.repository.BarberRepository;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.repository.ServiceRepository;
import com.marcedev.barberapp.security.BusinessAccessGuard;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/barbers")
@RequiredArgsConstructor
public class BarberController {

    private final BarberRepository barberRepository;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessAccessGuard businessAccessGuard;

    @GetMapping
    public List<BarberResponse> getByBusiness(
            @RequestParam Long businessId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        businessAccessGuard.assertBusinessAccess(businessId);

        List<Barber> barbers;
        if (includeInactive) {
            barbers = barberRepository.findWithServicesByBusinessId(businessId);
        } else if (serviceId != null) {
            barbers = barberRepository.findWithServicesByBusinessIdAndActiveTrueAndServices_Id(businessId, serviceId);
        } else {
            barbers = barberRepository.findWithServicesByBusinessIdAndActiveTrue(businessId);
        }

        if (includeInactive && serviceId != null) {
            barbers = barbers.stream()
                    .filter(b -> b.getServices().stream().anyMatch(s -> s.getId().equals(serviceId)))
                    .collect(Collectors.toList());
        }

        return barbers.stream().map(BarberResponse::from).toList();
    }

    @PostMapping
    public BarberResponse create(
            @RequestParam Long businessId,
            @RequestParam String name
    ) {
        businessAccessGuard.assertBusinessAccess(businessId);

        var business = businessRepository.findById(businessId)
                .orElseThrow();

        Barber barber = Barber.builder()
                .name(name)
                .business(business)
                .active(true)
                .build();

        return BarberResponse.from(barberRepository.save(barber));
    }

    @PutMapping("/{id}")
    public BarberResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BarberUpdateRequest request
    ) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
        businessAccessGuard.assertBusinessAccess(barber.getBusiness().getId());
        barber.setName(request.name());
        return BarberResponse.from(barberRepository.save(barber));
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
        businessAccessGuard.assertBusinessAccess(barber.getBusiness().getId());
        barber.setActive(false);
        barberRepository.save(barber);
    }

    @PutMapping("/{id}/activate")
    public BarberResponse activate(@PathVariable Long id) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
        businessAccessGuard.assertBusinessAccess(barber.getBusiness().getId());
        barber.setActive(true);
        return BarberResponse.from(barberRepository.save(barber));
    }

    @PutMapping("/{id}/services")
    public BarberResponse setServices(
            @PathVariable Long id,
            @Valid @RequestBody BarberServicesRequest request
    ) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));

        businessAccessGuard.assertBusinessAccess(barber.getBusiness().getId());

        var serviceIds = request.serviceIds();
        var services = serviceRepository.findAllById(serviceIds);
        if (services.size() != serviceIds.size()) {
            throw new IllegalArgumentException("Servicios inválidos");
        }
        boolean invalidBusiness = services.stream()
                .anyMatch(s -> !s.getBusiness().getId().equals(barber.getBusiness().getId()));
        if (invalidBusiness) {
            throw new IllegalArgumentException("Servicios fuera del negocio");
        }

        barber.setServices(services.stream().collect(java.util.stream.Collectors.toSet()));
        return BarberResponse.from(barberRepository.save(barber));
    }
}
