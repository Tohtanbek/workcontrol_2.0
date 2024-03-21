package com.tosDev.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignEquipDto {

    private Long id;
    private String worker;
    private String equipment;
    private Long equipId;
    private Float amount;
    private String startDateTime;
    private String endDateTime;
    private String status;
    private String totalAssigned;
}
