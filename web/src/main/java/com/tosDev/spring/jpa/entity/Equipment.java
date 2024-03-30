package com.tosDev.spring.jpa.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private Float amount;
    private Float total;
    private Float price4each;
    private Float totalLeft;
    private Float amountLeft;
    private String unit;
    private Float givenAmount;
    private Float givenTotal;
    private String link;
    private String source;

    @JsonFormat(pattern = "MM.dd.yyyy")
    private LocalDate supplyDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "equipment",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentEquip> assignmentEquipList = new ArrayList<>();

}
