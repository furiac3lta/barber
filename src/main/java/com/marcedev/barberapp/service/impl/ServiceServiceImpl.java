package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.CreateServiceRequest;
import com.marcedev.barberapp.dto.ServiceResponse;
import com.marcedev.barberapp.dto.UpdateServiceRequest;
import com.marcedev.barberapp.entity.Business;
import com.marcedev.barberapp.entity.ServiceItem;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.repository.ServiceRepository;
import com.marcedev.barberapp.service.ServiceService;
import com.marcedev.barberapp.security.BusinessAccessGuard;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final BusinessAccessGuard businessAccessGuard;

    @Override
    public ServiceResponse create(CreateServiceRequest request) {

        businessAccessGuard.assertBusinessAccess(request.businessId());

        if (serviceRepository.existsByBusinessIdAndNameIgnoreCaseAndActiveTrue(
                request.businessId(), request.name())) {
            throw new IllegalArgumentException("El servicio ya existe");
        }

        Business business = businessRepository.findById(request.businessId())
                .orElseThrow(() -> new EntityNotFoundException("Barbería no encontrada"));

        ServiceItem service = new ServiceItem();
        service.setName(request.name());
        service.setDurationMin(request.durationMin());
        service.setPrice(request.price());
        service.setBreakMin(request.breakMin() == null ? 0 : request.breakMin());
        service.setBusiness(business);
        service.setActive(true);

        return ServiceResponse.from(serviceRepository.save(service));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> getByBusiness(Long businessId) {

        businessAccessGuard.assertBusinessAccess(businessId);
        return serviceRepository.findByBusinessIdAndActiveTrue(businessId)
                .stream()
                .map(ServiceResponse::from)
                .toList();
    }

    @Override
    public void deactivate(Long serviceId) {
        ServiceItem service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        businessAccessGuard.assertBusinessAccess(service.getBusiness().getId());
        service.setActive(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> list(Long businessId) {

        businessAccessGuard.assertBusinessAccess(businessId);
        return serviceRepository.findByBusinessIdAndActiveTrue(businessId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ServiceResponse mapToResponse(ServiceItem s) {
        return new ServiceResponse(
                s.getId(),
                s.getName(),
                s.getDurationMin(),
                s.getPrice(),
                s.getBreakMin(),
                s.isActive()
        );
    }

    // ✅ ESTE ES EL QUE TE FALTABA
    @Override
    public ServiceResponse update(Long serviceId, UpdateServiceRequest request) {

        ServiceItem service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        businessAccessGuard.assertBusinessAccess(service.getBusiness().getId());
        service.setName(request.name());
        service.setDurationMin(request.durationMin());
        service.setPrice(request.price());
        if (request.breakMin() != null) {
            service.setBreakMin(request.breakMin());
        }

        return ServiceResponse.from(service);
    }


}
