package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceItem, Long> {

    List<ServiceItem> findByBusinessId(Long businessId);

    boolean existsByIdAndBusinessId(Long id, Long businessId);

    boolean existsByBusinessIdAndNameIgnoreCaseAndActiveTrue(Long businessId, String name);

    List<ServiceItem> findByBusinessIdAndActiveTrue(Long businessId);

    long countByBusinessIdAndActiveTrue(Long businessId); // ✅ agregar
}
