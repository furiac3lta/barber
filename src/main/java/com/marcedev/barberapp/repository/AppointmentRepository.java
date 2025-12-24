package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.Appointment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByBusinessIdAndDateOrderByStartTimeAsc(
            Long businessId,
            LocalDate date
    );

    List<Appointment> findByBusinessIdAndDate(
            Long businessId,
            LocalDate date
    );

    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.business.id = :businessId
          AND a.date = :date
          AND a.status <> 'CANCELED'
          AND a.startTime < :end
          AND a.endTime > :start
    """)
    boolean existsOverlapping(
            @Param("businessId") Long businessId,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end
    );
    List<Appointment> findByBusinessIdAndDateBetween(
            Long businessId,
            LocalDate start,
            LocalDate end
    );

}
