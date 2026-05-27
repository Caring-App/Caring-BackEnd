package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class RegisterWardRequestDto {
    private String name;
    private String phone;
    private String authNumber;
    private String password;
    private String passwordCheck;
    private LocalDate birthDate;
    private String address;
    private List<String> diseases;
}
