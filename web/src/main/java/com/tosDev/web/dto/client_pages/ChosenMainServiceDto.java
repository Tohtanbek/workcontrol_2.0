package com.tosDev.web.dto.client_pages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChosenMainServiceDto {
    Integer serviceId;
    Float total;
    Float area;
}
