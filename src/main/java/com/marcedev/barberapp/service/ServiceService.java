package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.CreateServiceRequest;
import com.marcedev.barberapp.dto.ServiceResponse;
import com.marcedev.barberapp.dto.UpdateServiceRequest;

import java.util.List;

public interface ServiceService {

    ServiceResponse create(CreateServiceRequest request);

    List<ServiceResponse> getByBusiness(Long businessId);

    void deactivate(Long serviceId);

    List<ServiceResponse> list(Long businessId);

    ServiceResponse update(Long serviceId, UpdateServiceRequest request);

}
