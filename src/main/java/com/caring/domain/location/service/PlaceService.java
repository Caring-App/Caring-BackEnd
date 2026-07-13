package com.caring.domain.location.service;

import com.caring.domain.connection.repository.ConnectionRepository;
import com.caring.domain.location.dto.PlaceRequestDto;
import com.caring.domain.location.dto.PlaceResponseDto;
import com.caring.domain.location.entity.Place;
import com.caring.domain.location.repository.PlaceRepository;
import com.caring.domain.member.entity.Member;
import com.caring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final MemberRepository memberRepository;
    private final ConnectionRepository connectionRepository;

    private void validateProtectorOfWard(Long protectorId, Long wardId) {
        boolean isConnected = connectionRepository.existsByProtector_MemberIdAndWard_MemberId(protectorId, wardId);
        if (!isConnected) {
            throw new IllegalArgumentException("해당 돌봄대상자에 대한 권한이 없습니다.");
        }
    }


    @Transactional
    public PlaceResponseDto createPlace(Long protectorId, PlaceRequestDto requestDto) {
        validateProtectorOfWard(protectorId, requestDto.getWardId());

        Member ward = memberRepository.findById(requestDto.getWardId())
                .orElseThrow(() -> new IllegalArgumentException("해당 돌봄대상자가 존재하지 않습니다. ID = " + requestDto.getWardId()));

        Place place = Place.builder()
                .ward(ward)
                .placeName(requestDto.getPlaceName())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .build();

        return new PlaceResponseDto(placeRepository.save(place));
    }


    public List<PlaceResponseDto> getPlaces(Long protectorId, Long wardId) {
        validateProtectorOfWard(protectorId, wardId);
        return placeRepository.findByWard_MemberId(wardId)
                .stream()
                .map(PlaceResponseDto::new)
                .toList();
    }


    @Transactional
    public void deletePlace(Long protectorId, Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다. ID = " + placeId));

        validateProtectorOfWard(protectorId, place.getWard().getMemberId());

        placeRepository.delete(place);
    }
}
