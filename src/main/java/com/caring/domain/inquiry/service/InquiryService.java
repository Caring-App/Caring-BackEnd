package com.caring.domain.inquiry.service;

import com.caring.domain.inquiry.dto.InquiryCreateRequest;
import com.caring.domain.inquiry.entity.Inquiry;
import com.caring.domain.inquiry.repository.InquiryRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}