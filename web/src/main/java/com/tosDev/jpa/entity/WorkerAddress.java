package com.tosDev.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class WorkerAddress {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    Worker worker;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    Address address;
}
