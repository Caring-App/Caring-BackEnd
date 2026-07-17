package com.caring.domain.report.service;

import com.caring.domain.connection.entity.Connection;
import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import com.caring.domain.report.dto.MoodCheckRequestDto;
import com.caring.domain.report.dto.MoodCheckResponseDto;
import com.caring.domain.report.entity.MoodCheck;
import com.caring.domain.report.repository.MoodCheckRepository;
import com.caring.domain.report.repository.ReportSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoodCheckService {

    private final MoodCheckRepository moodCheckRepository;
    private final MemberRepository memberRepository;
    private final ConnectionRepository connectionRepository;
    private final ReportSettingService reportSettingService;

    /**
            * 돌봄대상자 본인이 오늘의 기분 상태 기록/수정 (upsert)
     * - 연결된 보호자의 레포트 시간(= 마감시간) 이후에는 수정 불가
     */
    @Transactional
    public void checkMood(Long wardId, MoodCheckRequestDto requestDto) {

        Member ward = memberRepository.findById(wardId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 돌봄대상자입니다."));

        LocalDate today = LocalDate.now();

        // 연결된 보호자가 있으면 마감시간 체크
        connectionRepository.findByWard(ward).ifPresent(connection -> {
                    Long protectorId = connection.getProtector().getMemberId();
                    LocalTime deadline = reportSettingService.getEffectiveReportTime(wardId);

                    if (!LocalTime.now().isBefore(deadline)) {
                        throw new IllegalStateException("지금은 상태를 수정할 수 없습니다.");
                    }
                });

        // 오늘 기록이 있으면 수정, 없으면 새로 생성
        Optional<MoodCheck> existing = moodCheckRepository.findByWardIdAndRecordDate(wardId, today);

        if(existing.isPresent()){
            existing.get().updateMood(requestDto.getMoodStatus());
        } else {
            MoodCheck moodCheck = MoodCheck.builder()
                    .ward(ward)
                    .moodStatus(requestDto.getMoodStatus())
                    .recordDate(today)
                    .build();
            moodCheckRepository.save(moodCheck);
        }
    }

    /**
     * 보호자가 대상자의 오늘 기분 상태 조회
     */
    public MoodCheckResponseDto getTodayMood(Long protectorId, Long wardId) {

        // 1. 권한 검증 (연결 안 돼있으면 예외)
        Connection exsiting = connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId,wardId)
                .orElseThrow(()->new IllegalArgumentException("권한이 없습니다."));

        // 2. 오늘 기록 조회 (없으면 예외)
        LocalDate today = LocalDate.now();
        MoodCheck todayMood = moodCheckRepository.findByWardIdAndRecordDate(wardId,today)
                .orElseThrow(()->new IllegalArgumentException("아직 오늘의 기록이 없습니다."));

        // 3. DTO로 변환해서 반환
        return MoodCheckResponseDto.builder()
                .moodStatus(todayMood.getMoodStatus())
                .recordDate(todayMood.getRecordDate())
                .checkedAt(todayMood.getCheckedAt())
                .build();
    }
}
