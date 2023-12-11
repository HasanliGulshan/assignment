package com.justlife.assignment.entity;

import com.justlife.assignment.enums.Status;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CLEANER", schema = "public")
@Entity
public class Cleaner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToMany(mappedBy = "cleaners")
    private List<Booking> bookings = new ArrayList<>();
}
