package com.tosDev.web.dto.tableDto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressDto {
    private Integer id;
    private String shortName;
    private String fullName;
    private String zone;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> brigadiers;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> workers;
}
