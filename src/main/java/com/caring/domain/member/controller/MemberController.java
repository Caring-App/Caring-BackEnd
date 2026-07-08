package com.caring.domain.member.controller;

import com.caring.domain.member.dto.FcmTokenRequestDto;
import com.caring.domain.member.dto.LoginResponseDto;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PatchMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @AuthenticationPrincipal Long memberId,
            @RequestBody FcmTokenRequestDto requestDto
            ) {
        memberService.updateFcmToken(memberId, requestDto);
        return ResponseEntity.ok().build();
    }
}
