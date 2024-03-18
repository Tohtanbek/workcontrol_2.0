package com.tosDev.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BrigadierDtoWithSuper {
    private Integer id;
    private String name;
    private Long phoneNumber;
    private Float wageRate;
    private Float incomeRate;
    @JsonProperty(value = "isHourly")
    private boolean isHourly;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> supervisors;
}
