package com.caring.domain.member.service;

import com.caring.domain.member.dto.*;
import com.caring.domain.member.entity.*;
import com.caring.domain.member.repository.DiseaseRepository;
import com.caring.domain.member.repository.MemberDiseaseRepository;
import com.caring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final ProtectorCodeService protectorCodeService;
    private final MemberRepository memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;
    private final Map<String, String> smsVerificationStorage = new ConcurrentHashMap<>();

    // 보호자 회원가입
    @Transactional
    public RegisterProtectorResponseDto registerProtector(RegisterProtectorRequestDto requestDto){
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
                .password(requestDto.getPassword())
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
                .password(requestDto.getPassword())
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
        String fakeCode = "123456";

        smsVerificationStorage.put(phone, fakeCode);
        System.out.println("────── [SMS 발송 로그] ──────");
        System.out.println("수신번호: " + phone + " | 인증번호: " + fakeCode);
        System.out.println("───────────────────────────");
    }


    // 사용자가 입력한 인증번호 검증
    public boolean verifySmsCode(String phone, String code) {
        String realCode = smsVerificationStorage.get(phone);

        if(realCode == null) {
            return false;
        }

        if(realCode.equals(code)) {
            smsVerificationStorage.remove(phone);
            return true;
        }

        return false;
    }


    // 로그인
    public LoginResponseDto login(LoginRequestDto requestDto){
        Member member = memberRepository.findByPhone(requestDto.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("전화번호 또는 비밀번호가 일치하지 않습니다."));

        if(!member.getPassword().equals(requestDto.getPassword())){
            throw new IllegalArgumentException("전화번호 또는 비밀번호가 일치하지 않습니다.");
        }

        return LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .nickname(member.getNickname())
                .role(member.getRole())
                .authLevel(member.getAuthLevel())
                .build();
    }
}
