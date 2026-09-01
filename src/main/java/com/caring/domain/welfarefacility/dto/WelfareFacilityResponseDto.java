package com.caring.domain.welfarefacility.dto;

import com.caring.domain.welfarefacility.entity.WelfareFacility;
import lombok.Getter;

@Getter
public class WelfareFacilityResponseDto {
    private final String fcltNm;
    private final String fcltKindNm;
    private final String address;
    private final double distanceKm;

    public WelfareFacilityResponseDto(WelfareFacility facility, double distanceKm) {
        this.fcltNm = facility.getFcltNm();
        this.fcltKindNm = facility.getFcltKindNm();
        this.address = facility.getAddress();
        this.distanceKm = Math.round(distanceKm * 100) / 100.0;
    }
}
