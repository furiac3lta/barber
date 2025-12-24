package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    // ✅ ESTE método es el que te faltaba
    List<Availability> findByBusinessId(Long businessId);

    Optional<Availability> findByBusinessIdAndDayOfWeek(
            Long businessId,
            DayOfWeek dayOfWeek
    );
}
