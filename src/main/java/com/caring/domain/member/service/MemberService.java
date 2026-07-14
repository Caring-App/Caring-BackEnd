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
import java.util.Optional;
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
    private final SocialLoginService socialLoginService;


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

    // refreshToken 재발급
    @Transactional(readOnly = true)
    public RefreshTokenResponseDto reissueAccessToken(RefreshTokenRequestDto requestDto){

        String refreshToken = requestDto.getRefreshToken();

        // 1. 토큰 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)){
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. 토큰 타입이 refresh인지 확인
        if (!jwtUtil.getTokenType(refreshToken).equals("REFRESH")){
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        }

        // 3. 토큰에서 memberId 꺼내서 아직 존재하는 회원인지 확인
        Long memberId = jwtUtil.getMemberId(refreshToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new IllegalArgumentException("존재하는 회원이 아닙니다."));

        // 4. 새 accessToken 발급
        String newAccessToken = jwtUtil.generateAccessToken(member.getMemberId(),member.getRole().name());

        // 5. 반환
        return RefreshTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .build();
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

    // 소셜 회원가입
    @Transactional
    public SocialRegisterResponseDto socialRegister(SocialRegisterRequestDto requestDto){

        // 1. 이미 가입된 소셜 계정인지 확인
        memberRepository.findByProviderAndProviderId(requestDto.getProvider(),requestDto.getProviderId())
                .ifPresent(m->{
                    throw new IllegalArgumentException("이미 가입된 계정입니다.");
                });

        //2. 전화번호 중복 확인
        memberRepository.findByPhone(requestDto.getPhone())
                .ifPresent(m->{
                    throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
                });

        //3. protectorCode는 보호자일때만 생성
        String protectorCode = null;
        if (requestDto.getRole()==Role.PROTECTOR){
            protectorCode = protectorCodeService.generateCode();
        }

        //4. member 저장 ( 비밀번호는 없으니까 그냥 null로 남기기 )
        Member newmember = Member.builder()
                .provider(requestDto.getProvider())
                .providerId(requestDto.getProviderId())
                .role(requestDto.getRole())
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .birthDate(requestDto.getBirthDate())
                .address(requestDto.getAddress())
                .protectorCode(protectorCode)
                .authLevel(AuthLevel.USER)
                .build();

        Member savedMember = memberRepository.save(newmember);

        //5. 돌봄대상자라면 질병 저장
        List<String> savedDiseaseNames = new ArrayList<>();
        if(requestDto.getDiseases() != null) {
            for(String diseaseName : requestDto.getDiseases()) {
                diseaseRepository.findByDiseaseName(diseaseName).ifPresent(disease -> {
                            MemberDisease memberDisease = MemberDisease.builder()
                                    .ward(savedMember)
                                    .disease(disease)
                                    .build();

                            memberDiseaseRepository.save(memberDisease);
                            savedDiseaseNames.add(disease.getDiseaseName());
                });
            }
        }
        //6. JWT 발급
        String accessToken = jwtUtil.generateAccessToken(savedMember.getMemberId(), savedMember.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(savedMember.getMemberId(), savedMember.getRole().name());

        // 7. 응답 조합
        return SocialRegisterResponseDto.builder()
                .memberId(savedMember.getMemberId())
                .name(savedMember.getName())
                .phone(savedMember.getPhone())
                .role(savedMember.getRole())
                .protectorCode(protectorCode)
                .diseases(savedDiseaseNames)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    // 소셜 로그인
    @Transactional
    public SocialLoginResponseDto socialLogin(Provider provider, SocialLoginRequestDto requestDto){

        //1. 소셜 로그인에서 사용자 정보 받아오기
        SocialUserInfo socialUserInfo = socialLoginService.getSocialUserInfo(provider, requestDto.getAccessToken());

        //2. 이미 가입한 회원인지 조회
        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(provider, socialUserInfo.getProviderId());

        //3. 있으면 로그인 처리
        if (existingMember.isPresent()){
            Member member = existingMember.get(); // Optional 안에서 진짜 Member 꺼내기

            String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getRole().name());

            return
                    SocialLoginResponseDto.builder()
                    .isNewMember(false)
                    .memberId(member.getMemberId())
                    .name(member.getName())
                    .nickname(member.getNickname())
                    .role(member.getRole())
                    .authLevel(member.getAuthLevel())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            // 없으면 신규 회원으로 응답
            return SocialLoginResponseDto.builder()
                    .isNewMember(true)
                    .provider(socialUserInfo.getProvider())
                    .providerId(socialUserInfo.getProviderId())
                    .role(requestDto.getRole())
                    .build();
        }
    }

    // 비밀번호 재설정
    @Transactional
    public void resetPassword(PasswordResetRequestDto requestDto){

        // 1.SMS 인증 검증
        SmsVerification verification = smsVerificationStorage.get(requestDto.getPhone());

        if(verification == null
                || !verification.verified
                || verification.isExpired()
                || !verification.code.equals(requestDto.getAuthNumber())){
            throw new IllegalArgumentException("휴대폰 인증이 완료되지 않았습니다.");
        }

        // 2. 인증 정보 제거
        smsVerificationStorage.remove(requestDto.getPhone());

        // 3. newpassword와 newpasswordcheck 일치 확인
        if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 같지 않습니다.");
        }

        // 4.phone으로 회원 조회
        String phone= requestDto.getPhone();
        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 전화번호입니다."));

        // 5. 새 비밀번호 암호화해서 업데이트 메소드 호출
        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        member.updatePassword(encodedPassword);    }


    // FCM Token 발급
    @Transactional
    public void updateFcmToken(Long memberId, FcmTokenRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.updateFcmToken(requestDto.getFcmToken());
    }
}