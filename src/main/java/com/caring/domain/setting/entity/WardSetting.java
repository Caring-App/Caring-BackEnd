package com.caring.domain.setting.entity;

import com.caring.domain.member.entity.Member;
import com.caring.global.common.FontSize;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ward_setting")
public class WardSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ward_setting_id")
    private Long wardSettingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "font_size", nullable = false)
    private FontSize fontSize;

    @Column(name = "tts_rate", nullable = false)
    private Double ttsRate;

    @Builder
    public WardSetting(Member member,
                       FontSize fontSize,
                       Double ttsRate) {
        this.member = member;
        this.fontSize = fontSize;
        this.ttsRate = ttsRate;
    }

    public void updateSetting(FontSize fontSize, Double ttsRate) {
        this.fontSize = fontSize;
        this.ttsRate = ttsRate;
    }
}
