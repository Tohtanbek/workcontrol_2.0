package com.tosDev.jpa.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    //Следующие две аннотации нужны, чтобы использовать при десериализации просто поле name
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="name")
    @JsonIdentityReference(alwaysAsId=true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private EquipmentType type;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="name")
    @JsonIdentityReference(alwaysAsId=true)
    private Responsible responsible;

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
