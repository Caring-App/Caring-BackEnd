package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RegisterWardRequestDto {
    private String name;
    private String phone;
    private String authNumber;
    private String password;
    private String passwordCheck;
    private String address;
    private List<String> diseases;
}
