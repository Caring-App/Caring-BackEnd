package com.caring.domain.pill.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PillType {
    MORNING("아침약"),
    LUNCH("점심약"),
    DINNER("저녁약"),
    BEFORE_BED("취침전약");

    private final String description;
}
