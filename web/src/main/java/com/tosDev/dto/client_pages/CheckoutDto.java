package com.tosDev.dto.client_pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CheckoutDto {
    private Integer mainServiceId;
    private Integer[] extraServiceIds;
    private Float subTotal;
    private Float total;
    private String promoCode;
}
