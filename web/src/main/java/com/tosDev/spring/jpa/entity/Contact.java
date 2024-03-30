package com.tosDev.spring.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Contact {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;

    String name;

    @OneToMany(mappedBy = "contact")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Income> incomeList = new ArrayList<>();
}
