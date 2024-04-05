package com.tosDev.web.dto.client_pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShortServiceDto {
    private Integer id;
    private String name;
    private Float price;
    private Float minimalPrice;
}
