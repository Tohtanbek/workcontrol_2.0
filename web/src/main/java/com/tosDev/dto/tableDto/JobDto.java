package com.tosDev.dto.tableDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JobDto {
    private Integer id;

    private String name;
    private Float wageRate;
    private Float incomeRate;

    @JsonProperty("isHourly")
    private boolean isHourly;
}
