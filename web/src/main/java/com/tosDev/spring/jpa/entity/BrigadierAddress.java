package com.tosDev.spring.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class BrigadierAddress {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;


    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    Brigadier brigadier;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    Address address;

}
