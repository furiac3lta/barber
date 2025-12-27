// ===============================
// AVAILABILITY SERVICE IMPL (CORREGIDO)
// ===============================
package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.AvailabilityDTO;
import com.marcedev.barberapp.repository.AvailabilityRepository;
import com.marcedev.barberapp.repository.BusinessRepository;
import com.marcedev.barberapp.service.AppointmentService;
import com.marcedev.barberapp.service.AvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final BusinessRepository businessRepository;
    private final AppointmentService appointmentService;

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityDTO> getAvailabilityByBusiness(Long businessId) {
        return availabilityRepository.findByBusinessId(businessId)
                .stream()
                .map(a -> new AvailabilityDTO(
                        a.getDayOfWeek(),
                        a.getStartTime(),
                        a.getEndTime()
                ))
                .toList();
    }

    @Override
    public void setAvailability(
            Long businessId,
            DayOfWeek dayOfWeek,
            String startTime,
            String endTime
    ) {
        var business = businessRepository.findById(businessId)
                .orElseThrow(() -> new EntityNotFoundException("Barbería no encontrada"));

        var availability = availabilityRepository
                .findByBusinessIdAndDayOfWeek(businessId, dayOfWeek)
                .orElseGet(() -> {
                    var a = new com.marcedev.barberapp.entity.Availability();
                    a.setBusiness(business);
                    a.setDayOfWeek(dayOfWeek);
                    return a;
                });

        availability.setStartTime(LocalTime.parse(startTime));
        availability.setEndTime(LocalTime.parse(endTime));

        availabilityRepository.save(availability);
    }

    @Override
    public List<String> getAvailableSlots(
            Long businessId,
            Long barberId,
            Long serviceId,
            LocalDate date
    ) {
        return appointmentService.getAvailableSlots(
                businessId,
                barberId,
                serviceId,
                date
        );
    }
}
