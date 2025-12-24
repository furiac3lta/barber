package com.marcedev.barberapp.dto;

import com.marcedev.barberapp.entity.Appointment;
import com.marcedev.barberapp.enum_.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponse(
        Long id,
        LocalDate date,
        LocalTime time,
        AppointmentStatus status,
        Long clientId,
        String clientName,
        String clientPhone,
        Long serviceId,
        String serviceName,
        Integer serviceDurationMin
) {

    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getDate(),
                a.getStartTime(),        // 👈 importante
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
