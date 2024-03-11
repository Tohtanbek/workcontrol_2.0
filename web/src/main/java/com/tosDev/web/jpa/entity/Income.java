package com.tosDev.web.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Income {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;

    String shortInfo;

    Float totalSum;
    String type;

    String status;

    @ManyToOne
    Address address;

    @ManyToOne
    Worker worker;

    @ManyToOne
    Contact contact;

}