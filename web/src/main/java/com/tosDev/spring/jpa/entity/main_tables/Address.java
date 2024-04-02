package com.tosDev.spring.jpa.entity.main_tables;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Address {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String shortName;
    private String fullName;

    private String zone;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BrigadierAddress> brigadierAddressList = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WorkerAddress> workerAddressList = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "address", orphanRemoval = true)
    List<Shift> shiftList = new ArrayList<>();

    @OneToMany(mappedBy = "address")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Income> incomeList = new ArrayList<>();

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<AddressJob> addressJobList = new ArrayList<>();
}
