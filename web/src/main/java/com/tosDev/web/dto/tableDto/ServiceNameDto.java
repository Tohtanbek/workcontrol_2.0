package com.tosDev.web.dto.tableDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ServiceNameDto {
    private Integer id;
    private String name;
}
