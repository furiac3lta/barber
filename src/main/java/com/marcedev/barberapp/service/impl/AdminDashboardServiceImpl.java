package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.AdminDashboardResponse;
import com.marcedev.barberapp.entity.Appointment;
import com.marcedev.barberapp.enum_.AppointmentStatus;
import com.marcedev.barberapp.repository.AppointmentRepository;
import com.marcedev.barberapp.repository.BarberRepository;
import com.marcedev.barberapp.repository.ServiceRepository;
import com.marcedev.barberapp.repository.UserRepository;
import com.marcedev.barberapp.service.AdminDashboardService;
import com.marcedev.barberapp.security.BusinessAccessGuard;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final BarberRepository barberRepository;
    private final UserRepository userRepository;
    private final BusinessAccessGuard businessAccessGuard;

    @Override
    public AdminDashboardResponse getDashboard(Long businessId, LocalDate date) {

        businessAccessGuard.assertBusinessAccess(businessId);

        if (businessId == null) throw new EntityNotFoundException("businessId requerido");
        if (date == null) date = LocalDate.now();

        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        // TODAY
        long todayTotal = appointmentRepository.countByBusinessIdAndDate(businessId, date);
        long todayReserved = appointmentRepository.countByBusinessIdAndDateAndStatus(businessId, date, AppointmentStatus.RESERVED);
        long todayCanceled = appointmentRepository.countByBusinessIdAndDateAndStatus(businessId, date, AppointmentStatus.CANCELED);
        long todayAttended = appointmentRepository.countByBusinessIdAndDateAndStatus(businessId, date, AppointmentStatus.ATTENDED);

        // WEEK
        long weekTotal = appointmentRepository.countByBusinessIdAndDateBetween(businessId, monday, sunday);
        long weekReserved = appointmentRepository.countByBusinessIdAndDateBetweenAndStatus(businessId, monday, sunday, AppointmentStatus.RESERVED);
        long weekCanceled = appointmentRepository.countByBusinessIdAndDateBetweenAndStatus(businessId, monday, sunday, AppointmentStatus.CANCELED);
        long weekAttended = appointmentRepository.countByBusinessIdAndDateBetweenAndStatus(businessId, monday, sunday, AppointmentStatus.ATTENDED);

        // COUNTS
        long activeServices = serviceRepository.countByBusinessIdAndActiveTrue(businessId);
        long activeBarbers = barberRepository.countByBusinessIdAndActiveTrue(businessId);

        long clientsTotal;
        try {
            clientsTotal = userRepository.countByBusinessId(businessId);
        } catch (Exception e) {
            // si tu tabla users no tiene business_id todavía
            clientsTotal = userRepository.count();
        }

        List<Appointment> last = appointmentRepository.findLastByBusinessAndDate(businessId, date)
                .stream()
                .limit(10)
                .toList();

        List<AdminDashboardResponse.AppointmentMini> lastMini = last.stream()
                .map(a -> new AdminDashboardResponse.AppointmentMini(
                        a.getId(),
                        a.getStartTime().toString(),
                        a.getStatus().name(),
                        a.getBarber() != null ? a.getBarber().getName() : "-",
                        a.getService() != null ? a.getService().getName() : "-",
                        a.getClient() != null ? a.getClient().getName() : "-",
                        a.getClient() != null ? a.getClient().getPhone() : "-"
                ))
                .toList();

        return new AdminDashboardResponse(
                businessId,
                date,
                new AdminDashboardResponse.Stats(todayTotal, todayReserved, todayCanceled, todayAttended),
                new AdminDashboardResponse.Stats(weekTotal, weekReserved, weekCanceled, weekAttended),
                new AdminDashboardResponse.Counts(activeServices, activeBarbers, clientsTotal),
                lastMini
        );
    }
}

