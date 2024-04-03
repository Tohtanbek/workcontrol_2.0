package com.tosDev.dto.client_pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartDto {
    private Integer mainServiceId;
    private String mainServiceName;
    private Float mainServiceArea;
    private String mainServiceTotal;

    private List<ChosenExtraServiceDto> extraServiceList;

}
