package com.marcedev.barberapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "barbers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Barber {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id")
    private Business business;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "barber_services",
            joinColumns = @JoinColumn(name = "barber_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<ServiceItem> services = new HashSet<>();
}
