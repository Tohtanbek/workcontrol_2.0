package com.tosDev.web.jpa.entity;

import com.tosDev.web.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AssignmentEquip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naming;

    @ManyToOne
    private Worker worker;

    @ManyToOne
    Equipment equipment;

    private Float amount;
    private Float total;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @Enumerated
    private AssignmentStatus status;

}
