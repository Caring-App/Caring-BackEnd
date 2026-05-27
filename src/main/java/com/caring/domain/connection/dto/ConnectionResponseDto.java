package com.caring.domain.connection.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConnectionResponseDto {

    // 서버가 응답하는 데이터를 담는 dto
    // 연결 성공 시 응답을 담음

    private Long connectionId; // 연결 ID
    private String wardName; // 대상자 이름
    private LocalDateTime linkedAt; // 연결된 시간

}
