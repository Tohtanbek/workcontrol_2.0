package com.tosDev.dto;

import com.tosDev.jpa.entity.Address;
import com.tosDev.jpa.entity.Brigadier;
import com.tosDev.jpa.entity.Worker;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
