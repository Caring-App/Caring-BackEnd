package com.caring.domain.inquiry.service;

import com.caring.domain.inquiry.dto.InquiryAnswerRequest;
import com.caring.domain.inquiry.dto.InquiryCreateRequest;
import com.caring.domain.inquiry.dto.InquiryResponseDto;
import com.caring.domain.inquiry.entity.Inquiry;
import com.caring.domain.inquiry.repository.InquiryRepository;
import com.caring.domain.member.entity.AuthLevel;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    // 문의하기 생성
    @Transactional
    public Long createInquiry(Member member, InquiryCreateRequest request) {

        // 1 role 체크: 보호자만 작성 가능
        if (member.getRole()!=Role.PROTECTOR) {
            throw new IllegalArgumentException("보호자만 작성 가능합니다");
        }

        // 2 Inquiry 엔티티 빌드
        Inquiry inquiry = Inquiry.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        // 3
        inquiryRepository.save(inquiry);

        return inquiry.getInquiryId();
    }

    // 내 문의 목록 조회
    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getMyInquiries(Member member) {

        List<Inquiry> inquiries = inquiryRepository.findByMemberOrderByCreatedAtDesc(member);

        return inquiries.stream()
                .map(InquiryResponseDto::from)
                .collect(Collectors.toList());
    }

    // 문의 상세 조회
    @Transactional(readOnly = true)
    public InquiryResponseDto getInquiryDetail(Member member, Long inquiryId) {

        // 1 inquiryId로 Inquiry 조회
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(()->new IllegalArgumentException("조회할 문의가 없습니다."));

        // 2 권한 체크: 본인 작성자이거나 && 관리자 role 인 경우만 통과

        boolean isOwner = inquiry.getMember().getMemberId().equals(member.getMemberId());
        boolean isAdmin = member.getAuthLevel() == AuthLevel.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }

        // 3) Dto 변환해서 반환
        return InquiryResponseDto.from(inquiry);
    }

    // 관리자 답변 등록
    @Transactional
    public void answerInquiry(Member member, Long inquiryId, InquiryAnswerRequest request) {

        // 1 관리자 권한 체크
        if (member.getAuthLevel()!=AuthLevel.ADMIN) {
            throw new IllegalArgumentException("관리자만 접근 가능합니다");
        }

        // 2 inquiryId로 Inquiry 조회
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("답변할 문의가 없습니다."));

        // 3 registerAnswer() 메소드 호출
        inquiry.registerAnswer(request.getAnswer());

    }
}