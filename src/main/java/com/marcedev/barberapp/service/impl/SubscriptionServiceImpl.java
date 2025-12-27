package com.marcedev.barberapp.service.impl;

import com.marcedev.barberapp.entity.Subscription;
import com.marcedev.barberapp.repository.SubscriptionRepository;
import com.marcedev.barberapp.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repo;

    @Override
    public void validateActive(Long businessId) {
        Subscription s = repo.findByBusinessId(businessId)
                .orElseThrow(() -> new RuntimeException("Sin suscripción"));

        if (s.getStatus() != Subscription.Status.ACTIVE ||
                s.getExpiresAt().isBefore(LocalDate.now())) {
            throw new RuntimeException("Suscripción vencida");
        }
    }
}
