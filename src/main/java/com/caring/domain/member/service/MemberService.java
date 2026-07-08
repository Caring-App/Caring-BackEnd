package com.caring.domain.member.service;

import com.caring.domain.member.dto.*;
import com.caring.domain.member.entity.*;
import com.caring.domain.member.repository.DiseaseRepository;
import com.caring.domain.member.repository.MemberDiseaseRepository;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final ProtectorCodeService protectorCodeService;
    private final MemberRepository memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Map<String, SmsVerification> smsVerificationStorage = new ConcurrentHashMap<>();

    private static class SmsVerification {
        private final String code;
        private final LocalDateTime expiredAt;
        private boolean verified;

        private SmsVerification(String code, LocalDateTime expiredAt) {
            this.code = code;
            this.expiredAt = expiredAt;
            this.verified = false;
        }

        private boolean isExpired() {
            return LocalDateTime.now().isAfter(expiredAt);
        }
    }

    // 보호자 회원가입
    @Transactional
    public RegisterProtectorResponseDto registerProtector(RegisterProtectorRequestDto requestDto){
        SmsVerification verification = smsVerificationStorage.get(requestDto.getPhone());

        if(verification == null
                || !verification.verified
                || verification.isExpired()
                || !verification.code.equals(requestDto.getAuthNumber())){
            throw new IllegalArgumentException("휴대폰 인증이 완료되지 않았습니다.");
        }

        smsVerificationStorage.remove(requestDto.getAuthNumber());

        memberRepository.findByPhone(requestDto.getPhone())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
                });

        if(!requestDto.getPassword().equals(requestDto.getPasswordCheck())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String uniqueCode = protectorCodeService.generateCode();

        Member newProtector = Member.builder()
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .birthDate(requestDto.getBirthDate())
                .address(requestDto.getAddress())
                .provider(Provider.LOCAL)
                .role(Role.PROTECTOR)
                .protectorCode(uniqueCode)
                .authLevel(AuthLevel.USER)
                .build();

        Member savedProtector = memberRepository.save(newProtector);

        return RegisterProtectorResponseDto.of(savedProtector);
    }


    // 돌봄대상자 회원가입
    @Transactional
    public RegisterWardResponseDto registerWard(RegisterWardRequestDto requestDto){
        SmsVerification verification = smsVerificationStorage.get(requestDto.getPhone());

        if(verification == null
                || !verification.verified
                || verification.isExpired()
                || !verification.code.equals(requestDto.getAuthNumber())){
            throw new IllegalArgumentException("휴대폰 인증이 완료되지 않았습니다.");
        }

        smsVerificationStorage.remove(requestDto.getAuthNumber());

        memberRepository.findByPhone(requestDto.getPhone())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
                });

        if(!requestDto.getPassword().equals(requestDto.getPasswordCheck())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Member newWard = Member.builder()
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .birthDate(requestDto.getBirthDate())
                .address(requestDto.getAddress())
                .provider(Provider.LOCAL)
                .role(Role.WARD)
                .authLevel(AuthLevel.USER)
                .build();

        Member savedWard = memberRepository.save(newWard);

        List<String> savedDiseaseNames = new ArrayList<>();
        if(requestDto.getDiseases() != null) {
            for(String diseaseName : requestDto.getDiseases()) {
                diseaseRepository.findByDiseaseName(diseaseName).ifPresent(disease -> {
                    MemberDisease memberDisease = MemberDisease.builder()
                            .ward(savedWard)
                            .disease(disease)
                            .build();

                    memberDiseaseRepository.save(memberDisease);
                    savedDiseaseNames.add(disease.getDiseaseName());
                });
            }
        }

        return RegisterWardResponseDto.of(savedWard, savedDiseaseNames);
    }


    // 가짜 SMS 발송 및 번호 저장
    public void sendFakeSms(String phone) {
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(3);

        smsVerificationStorage.put(phone, new SmsVerification(code, expiredAt));

        System.out.println("────── [SMS 발송 로그] ──────");
        System.out.println("수신번호: " + phone + " | 인증번호: " + code + " | 만료: " + expiredAt);
        System.out.println("───────────────────────────");
    }


    // 사용자가 입력한 인증번호 검증
    public boolean verifySmsCode(String phone, String code) {
        SmsVerification verification = smsVerificationStorage.get(phone);

        if(verification == null) {
            return false;
        }

        if(verification.isExpired()) {
            smsVerificationStorage.remove(phone);
            return false;
        }

        if(!verification.code.equals(code)) {
            return false;
        }

        verification.verified = true;
        return true;
    }


    // 로그인
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto){
        Member member = memberRepository.findByPhone(requestDto.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("전화번호 또는 비밀번호가 일치하지 않습니다."));

        if(!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("전화번호 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getRole().name());

        return LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .nickname(member.getNickname())
                .role(member.getRole())
                .authLevel(member.getAuthLevel())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}