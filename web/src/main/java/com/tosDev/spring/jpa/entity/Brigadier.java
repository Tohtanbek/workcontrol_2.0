package com.tosDev.spring.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Brigadier {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String name;
    private Long phoneNumber;

    private Float incomeRate;
    private Float wageRate;
    private boolean isHourly;

    private Long chatId;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "brigadier", cascade = CascadeType.ALL,orphanRemoval = true)
    List<BrigadierAddress> brigadierAddressList = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "brigadier", cascade = CascadeType.ALL,orphanRemoval = true)
    List<ResponsibleBrigadier> responsibleBrigadierList = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "brigadier")
    List<Shift> shiftList = new ArrayList<>();
}
