package com.marcedev.barberapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate expiresAt;

    @OneToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    public enum Plan { BASIC, PRO }
    public enum Status { ACTIVE, EXPIRED }
}
