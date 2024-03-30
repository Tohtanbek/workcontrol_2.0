package com.tosDev.spring.jpa.entity;

import com.tosDev.enums.ShiftEndTypeEnum;
import com.tosDev.enums.ShiftStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Enumerated
    private ShiftStatusEnum status;
    @Enumerated
    private ShiftEndTypeEnum type;

    @ManyToOne
    private Address address;

    @ManyToOne
    private Worker worker;

    @ManyToOne
    private Job job;

    @ManyToOne
    private Brigadier brigadier;

    private Float totalHours;

    @OneToOne
    private Expense expense;
    @OneToOne
    private Income income;


}
