package com.tosDev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WorkerDto {
    private Integer id;
    private String name;
    private long phoneNumber;
    private String job;
}
