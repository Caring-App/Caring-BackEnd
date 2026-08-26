package com.caring.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageUpdateRequest {

    private String address; // 변경 주소

    // 비밀번호 변경은 선택사항이기에 셋 다 null이면 안 바꾸는 것
    private String currentPassword; // 현재 비밀번호
    private String newPassword; // 새 비밀번호
    private String newPasswordCheck; // 새 비밀번호 확인

}
