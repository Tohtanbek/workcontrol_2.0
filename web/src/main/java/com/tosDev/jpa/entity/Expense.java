package com.tosDev.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    private Address address;

    @ManyToOne
    private Worker worker;

    @OneToOne
    private Shift shift;

}
