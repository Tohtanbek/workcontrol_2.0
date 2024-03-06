package com.tosDev.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BrigadierDtoWithSuper {
    private Integer id;
    private String name;
    private Long phoneNumber;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> supervisors;
}
