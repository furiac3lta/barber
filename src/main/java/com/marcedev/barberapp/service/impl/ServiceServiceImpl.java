package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.CreateServiceRequest;
import com.marcedev.barberapp.dto.ServiceResponse;
import com.marcedev.barberapp.entity.Business;
import com.marcedev.barberapp.entity.ServiceItem;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.repository.ServiceRepository;
import com.marcedev.barberapp.service.ServiceService;
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

    @Override
    public ServiceResponse create(CreateServiceRequest request) {

        if (serviceRepository.existsByBusinessIdAndNameIgnoreCaseAndActiveTrue(
                request.businessId(), request.name())) {
            throw new IllegalArgumentException("El servicio ya existe");
        }

        Business business = businessRepository.findById(request.businessId())
                .orElseThrow(() -> new EntityNotFoundException("Barbería no encontrada"));

        ServiceItem service = new ServiceItem();
        service.setName(request.name());
        service.setDurationMin(request.durationMin());
        service.setBusiness(business);
        service.setActive(true);

        return ServiceResponse.from(serviceRepository.save(service));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> getByBusiness(Long businessId) {
        return serviceRepository.findByBusinessIdAndActiveTrue(businessId)
                .stream()
                .map(ServiceResponse::from)
                .toList();
    }

    @Override
    public void deactivate(Long serviceId) {
        ServiceItem service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        service.setActive(false);
    }
}
