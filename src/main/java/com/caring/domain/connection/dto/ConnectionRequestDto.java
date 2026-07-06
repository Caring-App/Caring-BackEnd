package com.caring.domain.connection.dto;

import lombok.Getter;
@Getter
public class ConnectionRequestDto {

    // 클라이언트가 보내는 데이터 dto로
    // 보호자가 고유코드를 입력할 때 필요한 dto

    private String protectorCode; // 돌봄대상자가 입력하는 보호자 고유 코드

}
