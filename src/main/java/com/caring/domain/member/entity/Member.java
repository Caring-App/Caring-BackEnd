package com.caring.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String phone;

    private String password;

    @Column(nullable = false)
    private String name;

    private String nickname;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'LOCAL'")
    private Provider provider = Provider.LOCAL;

    @Column(name = "provider_id")
    private  String providerId;

    @Column(name = "protector_code",unique = true)
    private String protectorCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_level",nullable = false, columnDefinition = "ENUM('ADMIN', 'USER') DEFAULT 'USER'")
    private AuthLevel authLevel = AuthLevel.USER;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "push_enabled", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean pushEnabled = true; // 신규 가입 시 기본 켜짐

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updatePassword(String password){ this.password =password; }

    // 돌봄대상자 관리 - 돌봄대상자 정보 수정 메소드
    public void updateProfile(String nickname,
                              String name,
                              String phone,
                              String address) {
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // 마이페이지 - 개인 정보 수정 메소드
    public void updateContact(String phone, String address){
        this.phone = phone;
        this.address = address;
    }

    // 마이페이지 - 알람 on/off
    public void updatePush(){
        this.pushEnabled=!pushEnabled;
    }
}
