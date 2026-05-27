package com.caring.domain.member.controller;

import com.caring.domain.member.dto.*;
import com.caring.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    /*
     * 보호자 회원가입
     * POST http://localhost:8080/api/auth/register/protector
     */
    @PostMapping("/register/protector")
    public ResponseEntity<RegisterProtectorResponseDto> registerProtector(@RequestBody RegisterProtectorRequestDto requestDto){
        RegisterProtectorResponseDto responseDto= memberService.registerProtector(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /*
     * 돌봄대상자 회원가입
     * POST http://localhost:8080/api/auth/register/ward
     */
    @PostMapping("/register/ward")
    public ResponseEntity<RegisterWardResponseDto> registerWard(@RequestBody RegisterWardRequestDto requestDto){
        RegisterWardResponseDto responseDto = memberService.registerWard(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /*
     * 핸드폰 인증번호 발송 (가짜 문자 발송)
     * POST http://localhost:8080/api/auth/sms/send?phone=01012345678
     */
    @PostMapping("/sms/send")
    public ResponseEntity<String> sendSms(@RequestParam("phone") String phone) {
        memberService.sendFakeSms(phone);
        return ResponseEntity.ok("인증번호 123456이 발송되었습니다. (테스트용)");
    }

    /*
     * 핸드폰 인증번호 검증 (화면에서 '인증 확인' 누를 때 호출)
     * POST http://localhost:8080/api/auth/sms/verify?phone=01012345678&code=123456
     */
    @PostMapping("/sms/verify")
    public ResponseEntity<String> verifySms(@RequestParam("phone") String phone,
                                            @RequestParam("code") String code) {
        boolean isVerified = memberService.verifySmsCode(phone, code);

        if(!isVerified) {
            return ResponseEntity.badRequest().body("인증번호가 틀렸거나 만료되었습니다.");
        }
        return ResponseEntity.ok("휴대폰 인증 성공");
    }

    /*
     * 로그인
     * Post http://localhost:8080/api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = memberService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
