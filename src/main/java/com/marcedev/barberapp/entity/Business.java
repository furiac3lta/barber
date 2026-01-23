package com.marcedev.barberapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Business {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 180)
    private String address;

    @Column(nullable = false)
    private Boolean active;
}
