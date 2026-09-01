package com.caring.domain.welfarefacility.dto;

import com.caring.domain.welfarefacility.entity.WelfareFacility;
import lombok.Getter;

@Getter
public class WelfareFacilityResponseDto {
    private final String fcltNm;
    private final String fcltKindNm;
    private final String address;
    private final String telNo;
    private final String cprNm;
    private final String homepageAddr;
    private final double distanceKm;

    public WelfareFacilityResponseDto(WelfareFacility facility, double distanceKm) {
        this.fcltNm = facility.getFcltNm();
        this.fcltKindNm = facility.getFcltKindNm();
        this.address = facility.getAddress();
        this.telNo = facility.getTelNo();
        this.cprNm = facility.getCprNm();
        this.homepageAddr = facility.getHomepageAddr();
        this.distanceKm = Math.round(distanceKm * 100) / 100.0;
    }
}
