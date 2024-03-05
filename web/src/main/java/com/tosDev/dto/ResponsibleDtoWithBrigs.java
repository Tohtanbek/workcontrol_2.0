package com.tosDev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponsibleDtoWithBrigs {
    private Integer id;
    private String name;
    private Long phoneNumber;
    private List<String> brigadiers;
}
