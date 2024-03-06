package com.tosDev.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Shift {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String shortInfo;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;

    @ManyToOne
    private Address address;

    @ManyToOne
    private Worker worker;

    private String job;

    @ManyToOne
    private Brigadier brigadier;

    private Float totalHours;

    @OneToOne
    private Expense expense;


}
