package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.BusinessRequest;
import com.marcedev.barberapp.dto.BusinessResponse;
import com.marcedev.barberapp.dto.BusinessUpdateRequest;
import com.marcedev.barberapp.entity.Business;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;

    @Override
    public BusinessResponse create(BusinessRequest request) {
        Business business = Business.builder()
                .name(request.name())
                .description(request.description())
                .phone(request.phone())
                .address(request.address())
                .active(true)
                .build();

        Business saved = businessRepository.save(business);
        return toResponse(saved);
    }

    @Override
    public BusinessResponse getById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbería no encontrada"));
        return toResponse(business);
    }

    @Override
    public List<BusinessResponse> list() {
        return businessRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<BusinessResponse> listAll() {
        return businessRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BusinessResponse update(Long id, BusinessUpdateRequest request) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbería no encontrada"));

        business.setName(request.name());
        business.setDescription(request.description());
        business.setPhone(request.phone());
        business.setAddress(request.address());

        return toResponse(businessRepository.save(business));
    }

    @Override
    public BusinessResponse softDelete(Long id) {
        return setActive(id, false);
    }

    @Override
    public BusinessResponse setActive(Long id, boolean active) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Barbería no encontrada"));

        business.setActive(active);
        return toResponse(businessRepository.save(business));
    }

    private BusinessResponse toResponse(Business b) {
        return new BusinessResponse(
                b.getId(),
                b.getName(),
                b.getDescription(),
                b.getPhone(),
                b.getAddress(),
                b.getActive()
        );
    }
}
