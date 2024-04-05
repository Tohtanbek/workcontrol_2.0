package com.tosDev.web.dto.tableDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ServiceDto {
    private Integer id;
    private String name;
    private String category;
    private Float price;
    private Float minimalPrice;
    private String promoCode;
    private Integer promoCodeDiscount;
}
