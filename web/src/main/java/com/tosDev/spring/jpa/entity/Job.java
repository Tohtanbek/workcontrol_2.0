package com.tosDev.spring.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private Float wageRate;
    private Float incomeRate;

    private boolean isHourly;

    @OneToMany(mappedBy = "job",cascade = CascadeType.ALL,orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<AddressJob> addressJobList = new ArrayList<>();

    @OneToMany(mappedBy = "job")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Worker> workerList = new ArrayList<>();

    @OneToMany(mappedBy = "job")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Shift> shiftList = new ArrayList<>();

}

