package com.marcedev.barberapp.repository;

import com.marcedev.barberapp.entity.User;
import com.marcedev.barberapp.enum_.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);

    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    long countByBusinessId(Long businessId); // ✅ agregar (si User tiene business_id)
    long countByBusinessIdAndRole(Long businessId, Role role);

    List<User> findByRole(Role role);

    @EntityGraph(attributePaths = "business")
    List<User> findWithBusinessByRole(Role role);

    @EntityGraph(attributePaths = "business")
    Optional<User> findWithBusinessById(Long id);
}
