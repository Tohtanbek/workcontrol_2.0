package com.tosDev.web.dto.tableDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BrigadierSmallDto {
    private Integer id;
    private String name;
    private Long phoneNumber;
    private Float wageRate;
    private Float incomeRate;
    @JsonProperty("isHourly")
    private boolean isHourly;
}
