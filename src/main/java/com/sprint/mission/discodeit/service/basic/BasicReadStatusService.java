package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId()))
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        if (!channelRepository.existsById(request.channelId()))
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        if (readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId()).isPresent())
            throw new IllegalArgumentException("이미 해당 사용자와 채널의 읽음 상태가 존재합니다.");

        Instant lastReadAt = request.lastReadAt() != null ? request.lastReadAt() : Instant.now();
        ReadStatus readStatus = readStatusRepository.save(new ReadStatus(request.userId(), request.channelId(), lastReadAt));

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto findById(UUID id) {
        ReadStatus readStatus = getReadStatusOrThrow(id);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = getReadStatusOrThrow(id);

        Instant newLastReadAt = request.newLastReadAt() != null ? request.newLastReadAt() : Instant.now();
        readStatus.updateLastReadAt(newLastReadAt);

        readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public void delete(UUID id) {
        getReadStatusOrThrow(id);
        readStatusRepository.deleteById(id);
    }

    private ReadStatus getReadStatusOrThrow(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 읽음 상태입니다."));
    }
}
