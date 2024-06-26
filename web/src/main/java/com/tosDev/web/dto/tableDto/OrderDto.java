package com.tosDev.web.dto.tableDto;

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
    private Float promoTotal;
    private String clientName;
    private Long phoneNumber;
    private String email;
    private String address;
    private Float area;
    private String dateTime;
    private String orderDateTime;
    private String timeZone;
    private String promoCode;
}
