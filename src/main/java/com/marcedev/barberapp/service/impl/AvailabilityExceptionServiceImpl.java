// src/main/java/com/marcedev/barberapp/service/impl/AvailabilityExceptionServiceImpl.java
package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.AvailabilityExceptionRequest;
import com.marcedev.barberapp.dto.AvailabilityExceptionResponse;
import com.marcedev.barberapp.entity.AvailabilityException;
import com.marcedev.barberapp.entity.Business;
import com.marcedev.barberapp.repository.AvailabilityExceptionRepository;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.service.AvailabilityExceptionService;
import com.marcedev.barberapp.security.BusinessAccessGuard;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityExceptionServiceImpl implements AvailabilityExceptionService {

    private final AvailabilityExceptionRepository exceptionRepository;
    private final BusinessRepository businessRepository;
    private final BusinessAccessGuard businessAccessGuard;

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("H:mm[:ss]");

    @Override
    public AvailabilityExceptionResponse upsert(AvailabilityExceptionRequest req) {

        businessAccessGuard.assertBusinessAccess(req.businessId());

        Business business = businessRepository.findById(req.businessId())
                .orElseThrow(() -> new EntityNotFoundException("Barbería no encontrada"));

        Optional<AvailabilityException> existing =
                exceptionRepository.findByBusinessIdAndDate(req.businessId(), req.date());

        AvailabilityException ex = existing.orElseGet(() ->
                AvailabilityException.builder()
                        .business(business)
                        .date(req.date())
                        .build()
        );

        boolean closed = Boolean.TRUE.equals(req.closed());
        ex.setClosed(closed);

        if (closed) {
            ex.setStartTime(null);
            ex.setEndTime(null);
        } else {
            if (req.startTime() == null || req.endTime() == null) {
                throw new IllegalArgumentException("startTime y endTime son obligatorios cuando closed=false");
            }
            LocalTime start = parseTime(req.startTime());
            LocalTime end = parseTime(req.endTime());
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("endTime debe ser mayor a startTime");
            }
            ex.setStartTime(start);
            ex.setEndTime(end);
        }

        return AvailabilityExceptionResponse.from(exceptionRepository.save(ex));
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityExceptionResponse get(Long businessId, LocalDate date) {

        businessAccessGuard.assertBusinessAccess(businessId);
        AvailabilityException ex = exceptionRepository.findByBusinessIdAndDate(businessId, date)
                .orElseThrow(() -> new EntityNotFoundException("No hay excepción para esa fecha"));
        return AvailabilityExceptionResponse.from(ex);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<AvailabilityExceptionResponse> listRecent(Long businessId, int limit) {
        businessAccessGuard.assertBusinessAccess(businessId);
        int safeLimit = Math.min(Math.max(limit, 1), 30);
        return exceptionRepository
                .findByBusinessIdOrderByDateDesc(businessId, PageRequest.of(0, safeLimit))
                .stream()
                .map(AvailabilityExceptionResponse::from)
                .toList();
    }

    @Override
    public void delete(Long businessId, LocalDate date) {

        businessAccessGuard.assertBusinessAccess(businessId);
        AvailabilityException ex = exceptionRepository.findByBusinessIdAndDate(businessId, date)
                .orElseThrow(() -> new EntityNotFoundException("No hay excepción para borrar"));
        exceptionRepository.delete(ex);
    }

    private LocalTime parseTime(String value) {
        String v = value.trim();
        try {
            if (v.length() <= 5) return LocalTime.parse(v, HH_MM);
            return LocalTime.parse(v, HH_MM_SS);
        } catch (Exception e) {
            return LocalTime.parse(v);
        }
    }
}
