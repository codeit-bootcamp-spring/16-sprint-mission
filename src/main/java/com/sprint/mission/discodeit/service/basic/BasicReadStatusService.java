package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.*;
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

    @Override
    public ReadStatusResponse create(CreateReadStatusRequest request) {
        if (request == null
                || userRepository.findById(request.userId()) == null
                || channelRepository.findById(request.channelId()) == null) {
            throw new IllegalArgumentException(
                    "사용자 또는 채널을 찾을 수 없습니다."
            );
        }

        if (readStatusRepository.findByUserIdAndChannelId(
                request.userId(), request.channelId()) != null) {
            throw new IllegalArgumentException(
                    "이미 존재하는 읽음 상태입니다."
            );
        }

        ReadStatus status = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt() == null
                        ? Instant.now()
                        : request.lastReadAt()
        );

        return toResponse(readStatusRepository.save(status));
    }

    @Override
    public ReadStatusResponse findById(UUID id) {
        return toResponse(require(id));
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse update(
            UpdateReadStatusRequest request
    ) {
        if (request == null || request.lastReadAt() == null) {
            throw new IllegalArgumentException(
                    "읽음 시간을 입력하세요."
            );
        }

        ReadStatus status = require(request.id());
        status.update(request.lastReadAt());

        return toResponse(readStatusRepository.save(status));
    }

    @Override
    public void delete(UUID id) {
        require(id);
        readStatusRepository.delete(id);
    }

    private ReadStatus require(UUID id) {
        ReadStatus status = readStatusRepository.findById(id);

        if (status == null) {
            throw new IllegalArgumentException(
                    "읽음 상태를 찾을 수 없습니다."
            );
        }

        return status;
    }

    private ReadStatusResponse toResponse(ReadStatus status) {
        return new ReadStatusResponse(
                status.getId(),
                status.getUserId(),
                status.getChannelId(),
                status.getLastReadAt(),
                status.getCreatedAt(),
                status.getUpdatedAt()
        );
    }
}