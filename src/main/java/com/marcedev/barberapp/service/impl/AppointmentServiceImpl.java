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
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BarberRepository barberRepository;
    private final AvailabilityExceptionRepository availabilityExceptionRepository;

    // =========================
    // CREATE
    // =========================
    @Override
    public AppointmentResponse create(CreateAppointmentRequest req) {

        Business business = businessRepository.findById(req.businessId())
                .orElseThrow(() -> new EntityNotFoundException("Business no encontrado"));

        Barber barber = barberRepository.findById(req.barberId())
                .orElseThrow(() -> new EntityNotFoundException("Barbero no encontrado"));

        if (!barberRepository.existsByIdAndBusinessId(barber.getId(), business.getId())) {
            throw new IllegalArgumentException("El barbero no pertenece a la barbería");
        }

        User client = userRepository.findById(req.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        ServiceItem service = serviceRepository.findById(req.serviceId())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        if (!serviceRepository.existsByIdAndBusinessId(service.getId(), business.getId())) {
            throw new IllegalArgumentException("El servicio no pertenece a la barbería");
        }

        LocalDate date = req.date();
        LocalTime start = req.time();
        int duration = service.getDurationMin();
        LocalTime end = start.plusMinutes(duration);

        DayOfWeek dow = date.getDayOfWeek();

        Availability availability = availabilityRepository
                .findByBusinessIdAndDayOfWeek(business.getId(), dow)
                .orElseThrow(() -> new IllegalArgumentException("No hay disponibilidad"));

        if (start.isBefore(availability.getStartTime()) || end.isAfter(availability.getEndTime())) {
            throw new IllegalArgumentException("Turno fuera del horario");
        }

        boolean overlap = appointmentRepository
                .existsOverlappingByBusinessAndBarber(
                        business.getId(),
                        barber.getId(),
                        date,
                        start,
                        end
                );

        if (overlap) {
            throw new IllegalArgumentException("Turno ya ocupado");
        }

        Appointment appt = Appointment.builder()
                .business(business)
                .barber(barber)
                .client(client)
                .service(service)
                .date(date)
                .startTime(start)
                .endTime(end)
                .status(AppointmentStatus.RESERVED)
                .build();

        return mapToResponse(appointmentRepository.save(appt));
    }

    // =========================
    // GET BY DAY
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByDate(Long businessId, LocalDate date) {
        return appointmentRepository
                .findAllByBusinessIdAndDateOrderByStartTimeAsc(businessId, date)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // CANCEL / ATTEND
    // =========================
    @Override
    public AppointmentResponse cancel(Long id) {
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
        a.setStatus(AppointmentStatus.CANCELED);
        return mapToResponse(a);
    }

    @Override
    public AppointmentResponse attend(Long id) {
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
        a.setStatus(AppointmentStatus.ATTENDED);
        return mapToResponse(a);
    }

    // =========================
    // AVAILABLE SLOTS (FINAL)
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableSlots(
            Long businessId,
            Long barberId,
            Long serviceId,
            LocalDate date
    ) {

        ServiceItem service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        if (!barberRepository.existsByIdAndBusinessId(barberId, businessId)) {
            throw new IllegalArgumentException("Barbero no pertenece a la barbería");
        }

        int duration = service.getDurationMin();
        DayOfWeek dow = date.getDayOfWeek();

        Availability availability = availabilityRepository
                .findByBusinessIdAndDayOfWeek(businessId, dow)
                .orElseThrow(() -> new IllegalArgumentException("No hay disponibilidad"));

        LocalTime startWindow = availability.getStartTime();
        LocalTime endWindow = availability.getEndTime();

        Optional<AvailabilityException> ex =
                availabilityExceptionRepository.findByBusinessIdAndDate(businessId, date);

        if (ex.isPresent()) {
            if (ex.get().isClosed()) return List.of();
            if (ex.get().getStartTime() != null && ex.get().getEndTime() != null) {
                startWindow = ex.get().getStartTime();
                endWindow = ex.get().getEndTime();
            }
        }

        List<Appointment> taken =
                appointmentRepository.findByBusinessIdAndBarberIdAndDate(
                        businessId, barberId, date
                );

        List<String> slots = new ArrayList<>();

        LocalTime cursor = startWindow;
        LocalTime last = endWindow.minusMinutes(duration);

        while (!cursor.isAfter(last)) {

            LocalTime s = cursor;
            LocalTime e = s.plusMinutes(duration);

            boolean overlap = taken.stream()
                    .filter(a -> a.getStatus() != AppointmentStatus.CANCELED)
                    .anyMatch(a ->
                            a.getStartTime().isBefore(e)
                                    && a.getEndTime().isAfter(s)
                    );

            if (!overlap) {
                slots.add(s.toString());
            }

            cursor = cursor.plusMinutes(15);
        }

        return slots;
    }

    // =========================
    // WEEK
    // =========================
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

        Map<DayOfWeek, List<AppointmentResponse>> map = new LinkedHashMap<>();
        for (DayOfWeek d : DayOfWeek.values()) {
            map.put(d, new ArrayList<>());
        }

        appointments.forEach(a ->
                map.get(a.getDate().getDayOfWeek()).add(mapToResponse(a))
        );

        return map;
    }

    // =========================
    // BY PHONE
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByPhone(String phone) {
        return appointmentRepository
                .findByClientPhone(phone)
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    // =========================
    // MAPPER
    // =========================
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
}
