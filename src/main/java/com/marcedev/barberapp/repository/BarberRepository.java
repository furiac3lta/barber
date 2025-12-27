package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberRepository extends JpaRepository<Barber, Long> {
    List<Barber> findByBusinessIdAndActiveTrue(Long businessId);
    boolean existsByIdAndBusinessId(Long id, Long businessId);
}
