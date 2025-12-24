package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.*;
import com.marcedev.barberapp.entity.*;
import com.marcedev.barberapp.enum_.AppointmentStatus;
import com.marcedev.barberapp.repository.*;
import com.marcedev.barberapp.service.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AvailabilityRepository availabilityRepository;

    @Override
    public AppointmentResponse create(CreateAppointmentRequest req) {

        Business business = businessRepository.findById(req.businessId())
                .orElseThrow(() -> new EntityNotFoundException("Business no encontrado"));

        User client = userRepository.findById(req.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        ServiceItem service = serviceRepository.findById(req.serviceId())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        if (!serviceRepository.existsByIdAndBusinessId(service.getId(), business.getId())) {
            throw new IllegalArgumentException("El servicio no pertenece a esta barbería");
        }

        LocalDate date = req.date();
        LocalTime start = req.time();
        int duration = service.getDurationMin();
        LocalTime end = start.plusMinutes(duration);

        DayOfWeek dow = date.getDayOfWeek();
        Availability availability = availabilityRepository
                .findByBusinessIdAndDayOfWeek(business.getId(), dow)
                .orElseThrow(() -> new IllegalArgumentException("No hay disponibilidad para " + dow));

        if (start.isBefore(availability.getStartTime()) || end.isAfter(availability.getEndTime())) {
            throw new IllegalArgumentException("Turno fuera del horario");
        }

        if (appointmentRepository.existsOverlapping(business.getId(), date, start, end)) {
            throw new IllegalArgumentException("Turno ya ocupado");
        }

        Appointment appt = Appointment.builder()
                .business(business)
                .client(client)
                .service(service)
                .date(date)
                .startTime(start)
                .endTime(end)
                .status(AppointmentStatus.RESERVED)
                .build();

        return mapToResponse(appointmentRepository.save(appt));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByDate(Long businessId, LocalDate date) {
        return appointmentRepository
                .findAllByBusinessIdAndDateOrderByStartTimeAsc(businessId, date)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AppointmentResponse cancel(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));

        appointment.setStatus(AppointmentStatus.CANCELED);

        return mapToResponse(appointment);
    }


    @Override
    public AppointmentResponse attend(Long id) {
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
        a.setStatus(AppointmentStatus.ATTENDED);
        return mapToResponse(a);
    }

    @Override
    public List<String> getAvailableSlots(Long businessId, Long serviceId, LocalDate date) {

        ServiceItem service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        int duration = service.getDurationMin();
        DayOfWeek dow = date.getDayOfWeek();

        Availability av = availabilityRepository
                .findByBusinessIdAndDayOfWeek(businessId, dow)
                .orElseThrow(() -> new IllegalArgumentException("No hay disponibilidad"));

        List<Appointment> taken = appointmentRepository.findByBusinessIdAndDate(businessId, date);

        List<String> slots = new ArrayList<>();
        LocalTime t = av.getStartTime();
        LocalTime last = av.getEndTime().minusMinutes(duration);

        while (!t.isAfter(last)) {
            LocalTime start = t;
            LocalTime end = start.plusMinutes(duration);

            boolean overlap = taken.stream()
                    .filter(a -> a.getStatus() != AppointmentStatus.CANCELED)
                    .anyMatch(a -> a.getStartTime().isBefore(end) && a.getEndTime().isAfter(start));

            if (!overlap) slots.add(start.toString());
            t = t.plusMinutes(15);
        }
        return slots;
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getDate(),
                a.getStartTime(),
                a.getStatus(),
                a.getClient().getId(),
                a.getClient().getName(),
                a.getClient().getPhone(),
                a.getService().getId(),
                a.getService().getName(),
                a.getService().getDurationMin()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<DayOfWeek, List<AppointmentResponse>> getWeek(
            Long businessId,
            LocalDate date
    ) {
        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        List<Appointment> appointments =
                appointmentRepository.findByBusinessIdAndDateBetween(
                        businessId, monday, sunday
                );

        Map<DayOfWeek, List<AppointmentResponse>> result = new LinkedHashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            result.put(day, new ArrayList<>());
        }

        appointments.forEach(a -> {
            result.get(a.getDate().getDayOfWeek())
                    .add(mapToResponse(a));
        });

        return result;
    }


}
