package com.tosDev.dto.client_pages;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChosenExtraServiceDto {
    private Integer id;
    private String name;
    private String price;
}
