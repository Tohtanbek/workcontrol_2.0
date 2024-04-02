package com.tosDev.dto.tableDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDto {
    private Long id;
    private String shortInfo;
    private String status;
    private Float total;
    private Long phoneNumber;
    private String email;
    private String address;
    private Float area;
    private String dateTime;
}
