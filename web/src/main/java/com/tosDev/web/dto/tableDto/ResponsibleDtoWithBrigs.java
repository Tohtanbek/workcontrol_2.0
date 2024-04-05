package com.tosDev.web.dto.tableDto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponsibleDtoWithBrigs {
    private Integer id;
    private String name;
    private Long phoneNumber;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> brigadiers;
}
