package com.tosDev.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WorkerDto {
    private Integer id;
    private String name;
    private long phoneNumber;
    private String job;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> addresses;
}
