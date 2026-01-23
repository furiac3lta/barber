package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.DashboardSummaryResponse;

import com.marcedev.barberapp.enum_.Role;
import com.marcedev.barberapp.repository.*;
import com.marcedev.barberapp.service.DashboardService;
import com.marcedev.barberapp.security.BusinessAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BarberRepository barberRepository;
    private final BusinessAccessGuard businessAccessGuard;

    @Override
    public DashboardSummaryResponse getSummary(Long businessId) {

        businessAccessGuard.assertBusinessAccess(businessId);

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        long todayCount =
                appointmentRepository.countByBusinessIdAndDate(businessId, today);

        long weekCount =
                appointmentRepository.countByBusinessIdAndDateBetween(
                        businessId, monday, sunday
                );

        long clients =
                userRepository.countByBusinessIdAndRole(businessId, Role.CLIENT);

        long services =
                serviceRepository.count();

        long barbers =
                barberRepository.countByBusinessId(businessId);

        return new DashboardSummaryResponse(
                todayCount,
                weekCount,
                clients,
                services,
                barbers
        );
    }
}
