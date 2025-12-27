package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.AppointmentResponse;
import com.marcedev.barberapp.dto.CreateAppointmentRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AppointmentService {
    AppointmentResponse create(CreateAppointmentRequest req);
    List<AppointmentResponse> getByDate(Long businessId, LocalDate date);
    AppointmentResponse cancel(Long id);
    AppointmentResponse attend(Long id);
    List<String> getAvailableSlots(
            Long businessId,
            Long barberId,
            Long serviceId,
            LocalDate date
    );
    Map<DayOfWeek, List<AppointmentResponse>> getWeek(
            Long businessId,
            LocalDate date
    );
    List<AppointmentResponse> getByPhone(String phone);

}
