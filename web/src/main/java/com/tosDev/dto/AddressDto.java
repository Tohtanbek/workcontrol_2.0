package com.tosDev.dto;

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

    @EqualsAndHashCode.Exclude
    private List<String> brigadiers;

    @EqualsAndHashCode.Exclude
    private List<String> workers;
}
