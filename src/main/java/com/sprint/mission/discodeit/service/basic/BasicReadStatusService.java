package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;

import com.sprint.mission.discodeit.entity.ReadStatus;

import com.sprint.mission.discodeit.exception.ResourceNotFoundException;

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

    @Override
    public ReadStatusResponse create(
            CreateReadStatusRequest request
    ) {
        if (request == null
                || request.userId() == null
                || request.channelId() == null) {
            throw new IllegalArgumentException(
                    "사용자 ID와 채널 ID가 필요합니다."
            );
        }

        if (userRepository.findById(request.userId()) == null) {
            throw new ResourceNotFoundException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        if (channelRepository.findById(request.channelId()) == null) {
            throw new ResourceNotFoundException(
                    "채널을 찾을 수 없습니다."
            );
        }

        if (readStatusRepository.findByUserIdAndChannelId(
                request.userId(),
                request.channelId()
        ) != null) {
            throw new IllegalStateException(
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
        return toResponse(requireReadStatus(id));
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 없습니다."
            );
        }

        if (userRepository.findById(userId) == null) {
            throw new ResourceNotFoundException(
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
                    "수정할 읽음 시간이 필요합니다."
            );
        }

        ReadStatus status = requireReadStatus(request.id());

        status.update(request.lastReadAt());

        return toResponse(readStatusRepository.save(status));
    }

    @Override
    public void delete(UUID id) {
        requireReadStatus(id);
        readStatusRepository.delete(id);
    }

    private ReadStatus requireReadStatus(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "읽음 상태 ID가 없습니다."
            );
        }

        ReadStatus status = readStatusRepository.findById(id);

        if (status == null) {
            throw new ResourceNotFoundException(
                    "읽음 상태를 찾을 수 없습니다. ID: " + id
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