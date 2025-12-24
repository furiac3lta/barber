package com.marcedev.barberapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "services",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_service_business_name",
                        columnNames = {"business_id", "name"}
                )
        }
)
@Getter @Setter
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer durationMin;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id")
    private Business business;
}
