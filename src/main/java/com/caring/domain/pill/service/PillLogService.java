package com.caring.domain.pill.service;

import com.caring.domain.pill.dto.PillLogResponseDto;
import com.caring.domain.pill.entity.PillLog;
import com.caring.domain.pill.repository.PillLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PillLogService {

    private final PillLogRepository pillLogRepository;

    @Transactional
    public PillLogResponseDto confirmPill(Long pillLogId){

        //1. Pilllogid로 Pilllog 조회
        PillLog pillLog = pillLogRepository.findById(pillLogId)
                .orElseThrow(() -> new IllegalArgumentException("복약 기록이 없습니다."));

        //2. 이미 확인된거면 예외
        if (pillLog.isTaken()) {
            throw new IllegalArgumentException("이미 확인된 복약입니다.");
        }
        // 3. confirm 호출 -> 확인 처리
        pillLog.confirm();

        // 4. DTO로 변환해서 반환
        return new PillLogResponseDto(pillLog);

    }


}
