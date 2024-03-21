package com.tosDev.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipDto {
    private Long id;
    private String naming;
    private String type;
    private String responsible;
    private Float amount;
    private Float total;

    @JsonProperty("price4each")
    private Float priceForEach;

    private Float totalLeft;
    private Float amountLeft;
    private String unit;
    private Float givenAmount;
    private Float givenTotal;
    private String link;
    private String source;

    private String supplyDate;
}
