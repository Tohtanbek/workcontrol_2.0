package com.tosDev.dto;

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
    private int amount;
    private float total;

    @JsonProperty("price4each")
    private float priceForEach;

    private float totalLeft;
    private int amountLeft;
    private String unit;
    private int givenAmount;
    private int givenTotal;
    private String link;
    private String source;

    private String supplyDate;
}
