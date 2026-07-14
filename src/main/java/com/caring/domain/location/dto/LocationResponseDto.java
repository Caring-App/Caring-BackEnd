package com.caring.domain.location.dto;

import com.caring.domain.location.entity.LocationLog;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.xml.stream.Location;
import java.time.LocalDateTime;

@Getter
public class LocationResponseDto {
    private final Long locationId;
    private final double latitude;
    private final double longitude;
    private final Integer stayDuration;

    @JsonProperty("isVisitVerified")
    private final Boolean isVisitVerified;

    private final LocalDateTime recordedAt;

    public LocationResponseDto(LocationLog entity) {
        this.locationId = entity.getLocationId();
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
        this.stayDuration = entity.getStayDuration();
        this.isVisitVerified = entity.isVisitVerified();
        this.recordedAt = entity.getRecordedAt();
    }
}
