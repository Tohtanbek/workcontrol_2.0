package com.tosDev.dto.equip;

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
    private String naming;
    private String worker;
    private Integer workerId;
    private String equipment;
    private Long equipId;
    private Float amount;
    private String startDateTime;
    private String endDateTime;
    private String status;
    private Float total;
}
