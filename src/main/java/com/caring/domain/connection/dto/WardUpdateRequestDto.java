package com.caring.domain.connection.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WardUpdateRequestDto {
    private String nickname;
    private String name;
    private String phone;
    private String address;
}
