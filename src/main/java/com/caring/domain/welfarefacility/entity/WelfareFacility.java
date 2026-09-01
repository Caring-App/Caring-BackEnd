package com.caring.domain.welfarefacility.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "welfare_facility")
public class WelfareFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "welfare_facility_id")
    private Long welfareFacilityId;

    @Column(name = "fclt_cd", nullable = false, unique = true, length = 20)
    private String fcltCd;

    @Column(name = "fclt_nm", nullable = false, length = 100)
    private String fcltNm;

    @Column(name = "fclt_kind_nm", length = 100)
    private String fcltKindNm;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Builder
    public WelfareFacility(String fcltCd,
                           String fcltNm,
                           String fcltKindNm,
                           String address,
                           Double latitude,
                           Double longitude) {
        this.fcltCd = fcltCd;
        this.fcltNm = fcltNm;
        this.fcltKindNm = fcltKindNm;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
