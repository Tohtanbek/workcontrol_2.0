package com.tosDev.web.dto.client_pages;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tosDev.web.json_deserializer.JsZonedDateTimeDes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExtraServiceDateTimeDto {
    private Integer[] serviceIds;
    @JsonDeserialize(using = JsZonedDateTimeDes.class)
    private ZonedDateTime dateTime;
}
