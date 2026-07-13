package com.caring.domain.location.dto;

import com.caring.domain.location.entity.Place;
import lombok.Getter;

@Getter
public class PlaceResponseDto {
    private final  Long placeId;
    private final String placeName;
    private final Double latitude;
    private final Double longitude;

    public PlaceResponseDto(Place entity) {
        this.placeId = entity.getPlaceId();
        this.placeName = entity.getPlaceName();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
    }
}
