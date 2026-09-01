package com.caring.domain.welfarefacility.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "tel_no", length = 20)
    private String telNo;

    @Column(name = "cpr_nm", length = 100)
    private String cprNm;

    @Column(name = "homepage_addr", length = 255)
    private String homepageAddr;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public WelfareFacility(String fcltCd,
                           String fcltNm,
                           String fcltKindNm,
                           String address,
                           Double latitude,
                           Double longitude,
                           String telNo,
                           String cprNm,
                           String homepageAddr) {
        this.fcltCd = fcltCd;
        this.fcltNm = fcltNm;
        this.fcltKindNm = fcltKindNm;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.telNo = telNo;
        this.cprNm = cprNm;
        this.homepageAddr = homepageAddr;
        this.updatedAt = LocalDateTime.now();
    }

    public void refresh(String fcltNm,
                        String address,
                        Double latitude,
                        Double longitude,
                        String telNo, String cprNm, String homepageAddr) {
        this.fcltNm = fcltNm;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.telNo = telNo;
        this.cprNm = cprNm;
        this.homepageAddr = homepageAddr;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isStale(long validMonths) {
        return updatedAt.isBefore(LocalDateTime.now().minusMonths(validMonths));
    }
}
