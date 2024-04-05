package com.tosDev.web.spring.jpa.entity.main_tables;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class ResponsibleBrigadier {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    Responsible responsible;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    Brigadier brigadier;

}
