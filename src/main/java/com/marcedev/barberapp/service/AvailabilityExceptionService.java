// src/main/java/com/marcedev/barberapp/service/AvailabilityExceptionService.java
package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.AvailabilityExceptionRequest;
import com.marcedev.barberapp.dto.AvailabilityExceptionResponse;

import java.time.LocalDate;

public interface AvailabilityExceptionService {

    AvailabilityExceptionResponse upsert(AvailabilityExceptionRequest req);

    AvailabilityExceptionResponse get(Long businessId, LocalDate date);

    void delete(Long businessId, LocalDate date);
}
