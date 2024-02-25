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
    private String naming;
    private String type;
    private String responsible;
    private int amount;

    @JsonProperty("price4each")
    private float priceForEach;

    private String unit;
    private String link;
    private String source;

    @JsonProperty("supply-date")
    private String supplyDate;
}
