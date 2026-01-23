package com.marcedev.barberapp.dto;

public record DashboardSummaryResponse(
        long totalAppointmentsToday,
        long totalAppointmentsWeek,
        long totalClients,
        long totalServices,
        long totalBarbers
) {}
