package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponseDto createReadStatus(ReadStatusCreateRequestDto dto) {
        UUID userId = dto.getUserId();
        UUID channelId = dto.getChannelId();

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
            throw new IllegalArgumentException("이미 해당 사용자의 채널 읽음 상태가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(userId, channelId);
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);

        return toResponseDto(savedReadStatus);
    }

    @Override
    public ReadStatusResponseDto findReadStatus(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() ->
                        new IllegalArgumentException("읽음 상태를 찾을 수 없습니다."));

        return toResponseDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllReadStatusesByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return readStatusRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public ReadStatusResponseDto updateReadStatus(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() ->
                        new IllegalArgumentException("읽음 상태를 찾을 수 없습니다."));

        readStatus.markAsRead();

        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);

        return toResponseDto(savedReadStatus);
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        readStatusRepository.findById(readStatusId)
                .orElseThrow(() ->
                        new IllegalArgumentException("읽음 상태를 찾을 수 없습니다."));

        readStatusRepository.deleteById(readStatusId);
    }

    private ReadStatusResponseDto toResponseDto(ReadStatus readStatus) {
        return ReadStatusResponseDto.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUserId())
                .channelId(readStatus.getChannelId())
                .lastReadAt(readStatus.getLastReadAt())
                .createdAt(readStatus.getCreatedAt())
                .updatedAt(readStatus.getUpdatedAt())
                .build();
    }
}