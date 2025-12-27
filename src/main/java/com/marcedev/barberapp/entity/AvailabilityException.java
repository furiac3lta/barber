// ===============================
// ENTITY: AvailabilityException
// ===============================
package com.marcedev.barberapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "availability_exceptions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"business_id", "date"}
        )
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean closed;

    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id")
    private Business business;
}
