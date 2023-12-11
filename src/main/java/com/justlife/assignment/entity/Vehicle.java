package com.justlife.assignment.entity;

import com.justlife.assignment.enums.Status;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "VEHICLE", schema = "public")
@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy="vehicle")
    private List<Booking> bookings;
}
