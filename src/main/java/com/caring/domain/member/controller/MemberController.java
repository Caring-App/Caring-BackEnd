package com.caring.domain.member.controller;

import com.caring.domain.member.dto.FcmTokenRequestDto;
import com.caring.domain.member.dto.LoginResponseDto;
import com.caring.domain.member.dto.MyPageUpdateRequest;
import com.caring.domain.member.dto.ProtectorCodeResponseDto;
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

    @PatchMapping
    public ResponseEntity<Void> updateMypage(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MyPageUpdateRequest request
            ){
        memberService.updateMypage(memberId,request);
        return ResponseEntity.ok().build();
    }
    
    //보호자 본인의 고유코드 조회
    @GetMapping("/protector-code")
    public ResponseEntity<ProtectorCodeResponseDto> getProtectorCode(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberService.getProtectorCode(memberId));
    }
}
