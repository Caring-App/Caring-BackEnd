package com.caring.domain.connection.service;

import com.caring.domain.connection.dto.ConnectionRequestDto;
import com.caring.domain.connection.dto.ConnectionResponseDto;
import com.caring.domain.connection.entity.Connection;
import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    // 고유코드 입력 -> 대상자 찾기 -> 연결 저장

    // connection db 접근
    private final ConnectionRepository connectionRepository;
    //고유코드로 대상자 찾을 때 필요
    private final MemberRepository memberRepository;

    // 보호자와 돌봄대상자 연결하는 메소드
    @Transactional // DB 작업 오류시 롤백
    public ConnectionResponseDto connect(Long wardId, ConnectionRequestDto requestDto){

        // 보호자 찾기
        Member protector = memberRepository.findByProtectorCode(requestDto.getProtectorCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고유 코드입니다."));

        // 돌봄대상자 찾기
        Member ward = memberRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 이미 연결된 대상자인지 확인
        if (connectionRepository.existsByWard(ward)){
            throw new IllegalArgumentException("이미 다른 보호자와 연결된 대상자입니다.");
        }

        // 커넥션 저장
        Connection connection = Connection.builder()
                .protector(protector)
                .ward(ward)
                .build();
        connectionRepository.save(connection);

        // responseDto로 변환
        return ConnectionResponseDto.builder()
                .connectionId(connection.getConnectionId())
                .wardName(ward.getName())
                .linkedAt(connection.getLinkedAt())
                .build();

    }



}
