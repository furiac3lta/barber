package com.marcedev.barberapp.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
