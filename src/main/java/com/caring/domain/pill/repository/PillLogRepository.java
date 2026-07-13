package com.caring.domain.pill.repository;

import com.caring.domain.pill.entity.PillLog;
import com.caring.domain.pill.entity.PillSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PillLogRepository extends JpaRepository<PillLog,Long> {

    // 1. 오늘 로그가 이미 있는지 확인
    //   "findBy" + "PillSchedule" + "And" + "RecordDate"
    // - 결과가 없을 수도 있어서(아직 알림 안 보낸 스케줄) Optional로 감싸기
    Optional<PillLog> findByPillScheduleAndRecordDate(PillSchedule pillSchedule, LocalDate recordDate);

    //2. 재알림 대상 조회
    // - 조건이 3개(미확인 + 오늘자 + 재알림횟수 제한)에 "미만(<)" 비교까지 있어서
    //   메서드 이름 자동생성으로는 표현이 복잡해지므로 JPQL을 직접 작성(@Query)

    @Query("SELECT pl FROM PillLog pl " +
            // PillLog와 함께 연관된 pillSchedule도 한 번에 같이 조회 (JOIN FETCH)
            // -> 나중에 스케줄러에서 pillLog.getPillSchedule() 호출할 때 추가 쿼리 안 나가고 한 번에 끝남
            "JOIN FETCH pl.pillSchedule ps " +
            // pillSchedule에 연결된 ward(대상자 Member)까지 같이 미리 로딩
            // -> FCM 보낼 때 ward.getFcmToken(), ward.getName() 쓸 거라 미리 당겨옴
            "JOIN FETCH ps.ward " +
            // 아직 복약 확인 안 한 것만 (isTaken = false)
            "WHERE pl.isTaken = false " +
            // 오늘 날짜에 해당하는 로그만 (파라미터로 넘긴 today와 비교)
            "AND pl.recordDate = :today " +
            // 재알림 횟수가 아직 최대치(maxRetry)에 도달하지 않은 것만
            // -> 스케줄러가 여기서 maxRetry로 ESCALATED(4) 같은 값을 넘겨서
            //    "이미 에스컬레이션 끝난 로그"는 이 목록에서 자동으로 제외되게 만듦
            "AND pl.currentRetryCount < :maxRetry")
    List<PillLog>findPendingLogsByDate(@Param("today")LocalDate today, @Param("maxRetry") int maxRetry);
}
