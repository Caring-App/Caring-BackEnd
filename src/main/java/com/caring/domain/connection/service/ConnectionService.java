package com.caring.domain.connection.service;

import com.caring.domain.connection.dto.*;
import com.caring.domain.connection.entity.Connection;
import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberDiseaseRepository;
import com.caring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    // 고유코드 입력 -> 대상자 찾기 -> 연결 저장

    // connection db 접근
    private final ConnectionRepository connectionRepository;
    //고유코드로 대상자 찾을 때 필요
    private final MemberRepository memberRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;

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


    // 대상자 목록 조회
    public List<WardSummaryResponseDto> getConnectedWards(Long protectorId) {
        Member protector = memberRepository.findById(protectorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return connectionRepository.findByProtector(protector)
                .stream()
                .map(connection -> WardSummaryResponseDto.builder()
                        .connectionId(connection.getConnectionId())
                        .wardId(connection.getWard().getMemberId())
                        .wardName(connection.getWard().getName())
                        .nickname(connection.getWard().getNickname())
                        .linkedAt(connection.getLinkedAt())
                        .build())
                .toList();
    }


    // 대상자 상세 조회
    public WardDetailResponseDto getWardDetail(Long protectorId, Long wardId) {
        Connection connection = connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다."));

        Member ward = connection.getWard();

        List<String> diseases = memberDiseaseRepository.findByWard_MemberId(wardId)
                .stream()
                .map(memberDisease -> memberDisease.getDisease().getDiseaseName())
                .toList();

        return WardDetailResponseDto.builder()
                .connectionId(connection.getConnectionId())
                .wardId(ward.getMemberId())
                .wardName(ward.getName())
                .nickname(ward.getNickname())
                .phone(ward.getPhone())
                .birthDate(ward.getBirthDate())
                .address(ward.getAddress())
                .diseases(diseases)
                .linkedAt(connection.getLinkedAt())
                .build();
    }


    // 대상자 정보 수정
    @Transactional
    public WardDetailResponseDto updateWard(Long protectorId, Long wardId, WardUpdateRequestDto requestDto) {
           Connection connection = connectionRepository.findByProtector_MemberIdAndWard_MemberId(protectorId, wardId)
                   .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다."));

           Member ward = connection.getWard();
           ward.updateProfile(
                   requestDto.getNickname(),
                   requestDto.getName(),
                   requestDto.getPhone(),
                   requestDto.getAddress()
           );

           List<String> diseases = memberDiseaseRepository.findByWard_MemberId(wardId)
                   .stream()
                   .map(memberDisease -> memberDisease.getDisease().getDiseaseName())
                   .toList();

        return WardDetailResponseDto.builder()
                .connectionId(connection.getConnectionId())
                .wardId(ward.getMemberId())
                .wardName(ward.getName())
                .nickname(ward.getNickname())
                .phone(ward.getPhone())
                .birthDate(ward.getBirthDate())
                .address(ward.getAddress())
                .diseases(diseases)
                .linkedAt(connection.getLinkedAt())
                .build();
    }
}
