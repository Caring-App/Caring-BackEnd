package com.caring.domain.inquiry.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryCreateRequest {

    private String title;
    private String content;
}
