package com.caring.domain.connection.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConnectionStatusResponseDto {
    private boolean connected;
    private String protectorName;
}
