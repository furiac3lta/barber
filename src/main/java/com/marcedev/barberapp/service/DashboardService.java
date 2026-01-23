package com.marcedev.barberapp.service;


import com.marcedev.barberapp.dto.DashboardSummaryResponse;

public interface DashboardService {
    DashboardSummaryResponse getSummary(Long businessId);
}
