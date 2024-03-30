package com.tosDev.dto.tableDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class IncomeDto {
    private Integer id;

    private String shortInfo;
    private String contact;
    private Float totalSum;
    private String type;
    private String status;
    private String dateTime;
    private String address;
    private String worker;
    private String shift;
    private String zone;
}
