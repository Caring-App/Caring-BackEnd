package com.caring.domain.inquiry.repository;

import com.caring.domain.inquiry.entity.Inquiry;
import com.caring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 본인 문의 목록 조회
    List<Inquiry> findByMemberOrderByCreatedAtDesc(Member member);


}