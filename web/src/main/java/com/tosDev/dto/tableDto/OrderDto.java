package com.tosDev.dto.tableDto;

import com.tosDev.dto.client_pages.ChosenMainServiceDto;
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
    private String status;
    private Float total;
    private Float subTotal;
    private String clientName;
    private String phoneNumber;
    private String email;
    private String address;
    private Float area;
    private String dateTime;
    private String orderDateTime;
}
