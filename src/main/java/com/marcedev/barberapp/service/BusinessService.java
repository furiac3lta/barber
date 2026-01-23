package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.BusinessRequest;
import com.marcedev.barberapp.dto.BusinessResponse;
import com.marcedev.barberapp.dto.BusinessUpdateRequest;

import java.util.List;

public interface BusinessService {
    BusinessResponse create(BusinessRequest request);
    BusinessResponse getById(Long id);
    List<BusinessResponse> list();
    List<BusinessResponse> listAll();
    BusinessResponse update(Long id, BusinessUpdateRequest request);
    BusinessResponse softDelete(Long id);
    BusinessResponse setActive(Long id, boolean active);
}
