package com.tosDev.jpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String naming;
    @ManyToOne
    private EquipmentType type;
    private String responsible;
    private Integer amount;
    private Float total;
    private Float price4each;
    private Float totalLeft;
    private Float amountLeft;
    private String unit;
    private Integer givenAmount;
    private Float givenTotal;
    private String link;
    private String source;

    @JsonFormat(pattern = "MM.dd.yyyy")
    private LocalDate supplyDate;
}
