package com.caring.domain.location.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceRequestDto {
    private Long wardId;
    private String placeName;
    private Double latitude;
    private Double longitude;
}
