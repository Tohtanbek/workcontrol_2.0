package com.tosDev.web.dto.tableDto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WorkerDto {
    private Integer id;
    private String name;
    private long phoneNumber;
    private String job;
    private Integer jobId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> addresses;
}
