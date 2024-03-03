package com.tosDev.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    Integer id;

    String shortName;
    String fullName;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BrigadierAddress> brigadierAddressList = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WorkerAddress> workerAddressList = new ArrayList<>();
}
