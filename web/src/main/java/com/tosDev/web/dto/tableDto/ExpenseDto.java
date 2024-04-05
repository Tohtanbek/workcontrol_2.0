package com.tosDev.web.dto.tableDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExpenseDto {
    private Integer id;
    private String shortInfo;
    private Float totalSum;
    private String type;
    private String status;
    private String dateTime;
    private String address;
    private String worker;
    private String shift;
    private String zone;
}
