package com.tosDev.web.spring.jpa.entity.main_tables;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Worker {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String name;
    private Long phoneNumber;

    @ManyToOne
    private Job job;

    private Long chatId;

    private boolean readyToSendPhoto;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "worker",cascade = CascadeType.ALL,orphanRemoval = true)
    List<WorkerAddress> workerAddressList = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "worker")
    List<Shift> shiftList = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "worker")
    List<Income> incomeList = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "worker")
    List<AssignmentEquip> assignmentEquipList = new ArrayList<>();

}
