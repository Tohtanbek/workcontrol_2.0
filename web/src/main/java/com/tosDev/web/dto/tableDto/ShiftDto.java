package com.tosDev.web.dto.tableDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShiftDto {
    Integer id;
    private String shortInfo;
    private String startDateTime;
    private String endDateTime;
    private String status;
    private String address;
    private String worker;
    private String job;
    private String brigadier;
    private Float totalHours;
    private String zone;
    private String folderLink;
}
