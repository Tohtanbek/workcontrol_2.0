package com.tosDev.web.spring.jpa.entity.main_tables;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Expense {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String shortInfo;

    private Float totalSum;
    private String type;

    private String status;

    private LocalDateTime dateTime;

    @ManyToOne
    private Address address;

    @ManyToOne
    private Worker worker;

    @OneToOne
    private Shift shift;

}
