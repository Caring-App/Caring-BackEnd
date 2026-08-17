package com.caring.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FontSize {
    SMALL("작게"),
    MEDIUM("보통"),
    LARGE("크게");

    private final String description;
}
