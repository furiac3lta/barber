package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByActiveTrue();
}
