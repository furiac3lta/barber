// src/main/java/com/marcedev/barberapp/service/AvailabilityExceptionService.java
package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.AvailabilityExceptionRequest;
import com.marcedev.barberapp.dto.AvailabilityExceptionResponse;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityExceptionService {

    AvailabilityExceptionResponse upsert(AvailabilityExceptionRequest req);

    AvailabilityExceptionResponse get(Long businessId, LocalDate date);

    List<AvailabilityExceptionResponse> listRecent(Long businessId, int limit);

    void delete(Long businessId, LocalDate date);
}
