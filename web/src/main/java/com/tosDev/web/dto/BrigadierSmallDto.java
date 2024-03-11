package com.tosDev.web.dto;

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
}
