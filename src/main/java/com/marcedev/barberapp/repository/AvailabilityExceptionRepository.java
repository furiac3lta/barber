// ===============================
// REPOSITORY: AvailabilityExceptionRepository
// ===============================
package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.AvailabilityException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilityExceptionRepository
        extends JpaRepository<AvailabilityException, Long> {

    Optional<AvailabilityException> findByBusinessIdAndDate(
            Long businessId,
            LocalDate date
    );

    List<AvailabilityException> findByBusinessIdOrderByDateDesc(Long businessId, Pageable pageable);
}
