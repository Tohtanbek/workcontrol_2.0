package com.tosDev.web.spring.jpa.entity.main_tables;

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
public class Income {

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

    @ManyToOne
    private Contact contact;

    @OneToOne
    private Shift shift;

}