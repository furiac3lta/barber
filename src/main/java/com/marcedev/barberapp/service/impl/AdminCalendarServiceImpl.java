package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.AdminCalendarEventResponse;
import com.marcedev.barberapp.entity.Appointment;
import com.marcedev.barberapp.repository.AppointmentRepository;
import com.marcedev.barberapp.service.AdminCalendarService;
import com.marcedev.barberapp.security.BusinessAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCalendarServiceImpl implements AdminCalendarService {

    private final AppointmentRepository appointmentRepository;
    private final BusinessAccessGuard businessAccessGuard;

    @Override
    public List<AdminCalendarEventResponse> getCalendar(
            Long businessId,
            LocalDate from,
            LocalDate to,
            Long barberId
    ) {
        businessAccessGuard.assertBusinessAccess(businessId);

        return appointmentRepository
                .findForCalendar(businessId, from, to, barberId)
                .stream()
                .map(a -> new AdminCalendarEventResponse(
                        a.getId(),
                        a.getDate(),
                        a.getStartTime(),
                        a.getEndTime(),
                        a.getStatus().name(),
                        a.getBarber().getId(),
                        a.getBarber().getName(),
                        a.getService().getName(),
                        a.getClient().getName(),
                        a.getClient().getPhone()
                ))
                .toList();
    }
}
