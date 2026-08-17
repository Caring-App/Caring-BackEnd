package com.caring.domain.inquiry.controller;

import com.caring.domain.inquiry.dto.InquiryAnswerRequest;
import com.caring.domain.inquiry.dto.InquiryCreateRequest;
import com.caring.domain.inquiry.dto.InquiryResponseDto;
import com.caring.domain.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // 1) 문의 등록
    @PostMapping
    public ResponseEntity<Long> createInquiry(
            @AuthenticationPrincipal Long memberId,
            @RequestBody InquiryCreateRequest request) {

        Long inquiryId = inquiryService.createInquiry(memberId, request);
        return ResponseEntity.ok(inquiryId);
    }

    // 2) 내 문의 목록 조회
    @GetMapping("/me")
    public ResponseEntity<List<InquiryResponseDto>> getMyInquiries(
            @AuthenticationPrincipal Long memberId) {
        List<InquiryResponseDto> result = inquiryService.getMyInquiries(memberId);
        return ResponseEntity.ok(result);
    }

    // 3) 문의 상세 조회
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponseDto> getInquiryDetail(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inquiryId) {
        InquiryResponseDto result = inquiryService.getInquiryDetail(memberId, inquiryId);
        return ResponseEntity.ok(result);
    }

    // 4) 답변 등록
    @PatchMapping("/{inquiryId}/answer")
    public ResponseEntity<Void> answerInquiry(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerRequest request) {

        inquiryService.answerInquiry(memberId, inquiryId, request);
        return ResponseEntity.ok().build();
    }
}