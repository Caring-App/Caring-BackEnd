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
@Table(name = "setting")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(name = "is_push_enabled", nullable = false)
    private boolean isPushEnabled;

    @Column(name = "is_location_agreed", nullable = false)
    private boolean isLocationAgreed;

    @Column(name = "font_size", nullable = false)
    private FontSize fontSize;

    @Column(name = "tts_rate", nullable = false)
    private Double ttsRate;

    @Builder
    public Setting(Member member,
                   boolean isPushEnabled,
                   boolean isLocationAgreed,
                   FontSize fontSize,
                   Double ttsRate) {
        this.member = member;
        this.isPushEnabled = isPushEnabled;
        this.isLocationAgreed = isLocationAgreed;
        this.fontSize = fontSize;
        this.ttsRate = ttsRate;
    }

    public void updateSetting(FontSize fontSize, Double ttsRate) {
        this.fontSize = fontSize;
        this.ttsRate = ttsRate;
    }
}
