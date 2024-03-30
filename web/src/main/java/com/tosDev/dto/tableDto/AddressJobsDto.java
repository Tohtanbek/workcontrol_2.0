package com.tosDev.dto.tableDto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressJobsDto {
    private Integer id;
    private String name;
    private String jobs;
}
