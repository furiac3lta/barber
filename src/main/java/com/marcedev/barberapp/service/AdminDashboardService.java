package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.AdminDashboardResponse;

import java.time.LocalDate;

public interface AdminDashboardService {
    AdminDashboardResponse getDashboard(Long businessId, LocalDate date);
}
