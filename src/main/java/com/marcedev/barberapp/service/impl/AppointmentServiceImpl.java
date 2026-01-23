package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.dto.*;
import com.marcedev.barberapp.entity.*;
import com.marcedev.barberapp.enum_.AppointmentStatus;
import com.marcedev.barberapp.repository.*;
import com.marcedev.barberapp.service.AppointmentService;
import com.marcedev.barberapp.security.BusinessAccessGuard;
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
    private final BusinessAccessGuard businessAccessGuard;

    // =========================
    // MAPPER MANUAL (ÚNICO)
    // =========================
    private AppointmentResponse toResponse(Appointment a) {
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
                a.getService().getDurationMin(),

                // 🆕 BARBER
                a.getBarber().getId(),
                a.getBarber().getName()
        );
    }


    // =========================
    // CREATE
    // =========================
    @Override
    public AppointmentResponse create(CreateAppointmentRequest req) {

        businessAccessGuard.assertBusinessAccess(req.businessId());

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

        boolean hasServices = barber.getServices() != null && !barber.getServices().isEmpty();
        boolean offersService = barber.getServices() != null && barber.getServices().stream()
                .anyMatch(s -> s.getId().equals(service.getId()));
        if (hasServices && !offersService) {
            throw new IllegalArgumentException("El profesional no ofrece ese servicio");
        }

        LocalDate date = req.date();
        LocalTime start = req.time();
        int duration = service.getDurationMin();
        LocalTime end = start.plusMinutes(duration);

        DayOfWeek dow = date.getDayOfWeek();

        Availability availability = availabilityRepository
                .findByBusinessIdAndDayOfWeek(business.getId(), dow)
                .orElseThrow(() -> new IllegalArgumentException("No hay disponibilidad"));

        if (!isWithinAvailability(start, end, availability)) {
            throw new IllegalArgumentException("Turno fuera del horario");
        }

        boolean overlap = appointmentRepository.existsOverlappingByBusinessAndBarber(
                business.getId(),
                barber.getId(),
                date,
                start,
                end
        );

        if (overlap) {
            throw new IllegalArgumentException("Turno ya ocupado para este barbero");
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

        return toResponse(appointmentRepository.save(appt));
    }

    // =========================
    // GET BY DAY
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByDate(Long businessId, LocalDate date) {

        businessAccessGuard.assertBusinessAccess(businessId);
        return appointmentRepository
                .findAllByBusinessIdAndDateOrderByStartTimeAsc(businessId, date)
                .stream()
                .map(this::toResponse)
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
        return toResponse(a);
    }

    @Override
    public AppointmentResponse attend(Long id) {
        Appointment a = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
        a.setStatus(AppointmentStatus.ATTENDED);
        return toResponse(a);
    }

    // =========================
    // AVAILABLE SLOTS
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableSlots(
            Long businessId,
            Long barberId,
            Long serviceId,
            LocalDate date
    ) {

        businessAccessGuard.assertBusinessAccess(businessId);

        ServiceItem service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado"));

        if (!barberRepository.existsByIdAndBusinessId(barberId, businessId)) {
            throw new IllegalArgumentException("Barbero no pertenece a la barbería");
        }

        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new EntityNotFoundException("Barbero no encontrado"));
        boolean hasServices = barber.getServices() != null && !barber.getServices().isEmpty();
        boolean offersService = barber.getServices() != null && barber.getServices().stream()
                .anyMatch(s -> s.getId().equals(serviceId));
        if (hasServices && !offersService) {
            throw new IllegalArgumentException("El profesional no ofrece ese servicio");
        }

        int duration = service.getDurationMin();
        DayOfWeek dow = date.getDayOfWeek();

        Availability availability = availabilityRepository
                .findByBusinessIdAndDayOfWeek(businessId, dow)
                .orElseThrow(() -> new IllegalArgumentException("No hay disponibilidad"));

        List<TimeWindow> windows = buildWindows(availability);

        Optional<AvailabilityException> ex =
                availabilityExceptionRepository.findByBusinessIdAndDate(businessId, date);

        if (ex.isPresent()) {
            if (ex.get().isClosed()) return List.of();
            if (ex.get().getStartTime() != null && ex.get().getEndTime() != null) {
                windows = List.of(new TimeWindow(ex.get().getStartTime(), ex.get().getEndTime()));
            }
        }

        List<Appointment> taken =
                appointmentRepository.findByBusinessIdAndBarberIdAndDate(
                        businessId, barberId, date
                );

        List<String> slots = new ArrayList<>();

        for (TimeWindow window : windows) {
            LocalTime cursor = window.start();
            LocalTime last = window.end().minusMinutes(duration);

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
        }

        return slots;
    }

    private boolean isWithinAvailability(LocalTime start, LocalTime end, Availability availability) {
        var windows = buildWindows(availability);
        return windows.stream().anyMatch(w ->
                !start.isBefore(w.start()) && !end.isAfter(w.end())
        );
    }

    private List<TimeWindow> buildWindows(Availability availability) {
        List<TimeWindow> windows = new ArrayList<>();
        windows.add(new TimeWindow(availability.getStartTime(), availability.getEndTime()));
        if (availability.getStartTime2() != null && availability.getEndTime2() != null) {
            windows.add(new TimeWindow(availability.getStartTime2(), availability.getEndTime2()));
        }
        return windows;
    }

    private record TimeWindow(LocalTime start, LocalTime end) {}

    // =========================
    // WEEK
    // =========================
    @Override
    @Transactional(readOnly = true)
    public Map<DayOfWeek, List<AppointmentResponse>> getWeek(
            Long businessId,
            LocalDate date
    ) {

        businessAccessGuard.assertBusinessAccess(businessId);

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
                map.get(a.getDate().getDayOfWeek()).add(toResponse(a))
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
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // RESCHEDULE
    // =========================
    @Override
    @Transactional
    public AppointmentResponse reschedule(
            Long appointmentId,
            RescheduleAppointmentRequest request
    ) {

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));

        if (appt.getStatus() == AppointmentStatus.CANCELED) {
            throw new IllegalStateException("No se puede reprogramar un turno cancelado");
        }

        LocalDate newDate = LocalDate.parse(request.date());
        LocalTime newStart = LocalTime.parse(request.time());

        int duration = appt.getService().getDurationMin();
        LocalTime newEnd = newStart.plusMinutes(duration);

        boolean overlap = appointmentRepository.existsOverlapping(
                appt.getBusiness().getId(),
                appt.getBarber().getId(),
                newDate,
                newStart,
                newEnd,
                appt.getId()
        );

        if (overlap) {
            throw new IllegalArgumentException("Horario no disponible");
        }

        appt.setDate(newDate);
        appt.setStartTime(newStart);
        appt.setEndTime(newEnd);
        appt.setStatus(AppointmentStatus.RESERVED);

        return toResponse(appt);
    }
}
