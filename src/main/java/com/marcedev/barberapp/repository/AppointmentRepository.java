package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // =========================
    // EXISTENTE / GENERAL
    // =========================

    List<Appointment> findAllByBusinessIdAndDateOrderByStartTimeAsc(
            Long businessId,
            LocalDate date
    );

    List<Appointment> findByBusinessIdAndDate(
            Long businessId,
            LocalDate date
    );

    List<Appointment> findByBusinessIdAndDateBetween(
            Long businessId,
            LocalDate start,
            LocalDate end
    );

    List<Appointment> findByClientPhone(String phone);

    // =========================
    // 🔴 MULTI-BARBER (FALTABAN)
    // =========================

    List<Appointment> findByBusinessIdAndBarberIdAndDate(
            Long businessId,
            Long barberId,
            LocalDate date
    );

    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.business.id = :businessId
          AND a.barber.id = :barberId
          AND a.date = :date
          AND a.status <> 'CANCELED'
          AND a.startTime < :end
          AND a.endTime > :start
    """)
    boolean existsOverlappingByBusinessAndBarber(
            @Param("businessId") Long businessId,
            @Param("barberId") Long barberId,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end
    );

    // =========================
    // DASHBOARD (YA USADOS)
    // =========================

    long countByBusinessIdAndDate(Long businessId, LocalDate date);

    long countByBusinessIdAndDateAndStatus(
            Long businessId,
            LocalDate date,
            com.marcedev.barberapp.enum_.AppointmentStatus status
    );

    long countByBusinessIdAndDateBetween(
            Long businessId,
            LocalDate start,
            LocalDate end
    );

    long countByBusinessIdAndDateBetweenAndStatus(
            Long businessId,
            LocalDate start,
            LocalDate end,
            com.marcedev.barberapp.enum_.AppointmentStatus status
    );

    @Query("""
        select a
        from Appointment a
        where a.business.id = :businessId
          and a.date = :date
        order by a.startTime desc
    """)
    List<Appointment> findLastByBusinessAndDate(
            @Param("businessId") Long businessId,
            @Param("date") LocalDate date
    );

    @Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.business.id = :businessId
      AND a.barber.id = :barberId
      AND a.date = :date
      AND a.status <> 'CANCELED'
      AND a.id <> :excludeId
      AND a.startTime < :endTime
      AND a.endTime > :startTime
""")
    boolean existsOverlapping(
            Long businessId,
            Long barberId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Long excludeId
    );

    @Query("""
    select a from Appointment a
    where a.business.id = :businessId
      and a.date between :from and :to
      and (:barberId is null or a.barber.id = :barberId)
""")
    List<Appointment> findForCalendar(
            @Param("businessId") Long businessId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("barberId") Long barberId
    );


}
