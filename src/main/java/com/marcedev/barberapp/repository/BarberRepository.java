package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.Barber;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarberRepository extends JpaRepository<Barber, Long> {
    List<Barber> findByBusinessId(Long businessId);
    List<Barber> findByBusinessIdAndActiveTrue(Long businessId);
    List<Barber> findByBusinessIdAndActiveTrueAndServices_Id(Long businessId, Long serviceId);

    @EntityGraph(attributePaths = "services")
    List<Barber> findWithServicesByBusinessId(Long businessId);

    @EntityGraph(attributePaths = "services")
    List<Barber> findWithServicesByBusinessIdAndActiveTrue(Long businessId);

    @EntityGraph(attributePaths = "services")
    List<Barber> findWithServicesByBusinessIdAndActiveTrueAndServices_Id(Long businessId, Long serviceId);
    boolean existsByIdAndBusinessId(Long id, Long businessId);

    long countByBusinessId(Long businessId);
    long countByBusinessIdAndActiveTrue(Long businessId);

    Optional<Barber> findByUserId(Long userId);

    @EntityGraph(attributePaths = { "user", "business" })
    Optional<Barber> findWithUserById(Long id);

}
