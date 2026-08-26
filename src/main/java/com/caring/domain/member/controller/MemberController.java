package com.caring.domain.member.controller;

import com.caring.domain.member.dto.FcmTokenRequestDto;
import com.caring.domain.member.dto.LoginResponseDto;
import com.caring.domain.member.dto.MyPageUpdateRequest;
import com.caring.domain.member.dto.PhoneChangeRequestDto;
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

    // 보호자 푸시 알림 on.off
    @PostMapping("/push-toggle")
    public ResponseEntity<Void> togglePush(
            @AuthenticationPrincipal Long memberId
    ) {
        memberService.togglePush(memberId);
        return  ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(
            @AuthenticationPrincipal Long memberId) {

        memberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }

    // 마이페이지 - 전화번호 변경 (SMS 인증 필수)
    @PatchMapping("/phone")
    public ResponseEntity<Void> changePhone(
            @AuthenticationPrincipal Long memberId,
            @RequestBody PhoneChangeRequestDto requestDto
    ) {
        memberService.changePhone(memberId, requestDto);
        return ResponseEntity.ok().build();
    }
}
