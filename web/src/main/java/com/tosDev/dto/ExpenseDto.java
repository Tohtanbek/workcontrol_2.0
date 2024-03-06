package com.tosDev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExpenseDto {
    Integer id;
    private String shortInfo;
    private Float totalSum;
    private String type;
    private String status;
    private String address;
    private String worker;
    private String shift;
    private String zone;
}
