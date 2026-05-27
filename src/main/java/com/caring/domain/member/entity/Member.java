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
}
