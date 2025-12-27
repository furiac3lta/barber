// ===============================
// ENTITY: BarberAvailability
// ===============================
package com.marcedev.barberapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "barber_availability",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"barber_id", "day_of_week"}
        )
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarberAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "barber_id")
    private Barber barber;
}
