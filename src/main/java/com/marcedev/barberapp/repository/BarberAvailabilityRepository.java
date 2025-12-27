// ===============================
// REPOSITORY: BarberAvailabilityRepository
// ===============================
package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.BarberAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;

public interface BarberAvailabilityRepository
        extends JpaRepository<BarberAvailability, Long> {

    Optional<BarberAvailability> findByBarberIdAndDayOfWeek(
            Long barberId,
            DayOfWeek dayOfWeek
    );
}
