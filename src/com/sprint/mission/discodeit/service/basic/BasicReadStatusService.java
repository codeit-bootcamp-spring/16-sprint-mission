package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("읽음 상태 생성 정보는 필수입니다.");
        }

        Objects.requireNonNull(request.userId(), "사용자 ID는 필수입니다.");
        Objects.requireNonNull(request.channelId(), "채널 ID는 필수입니다.");

        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다.")
                );

        Optional<ReadStatus> existingStatus =
                readStatusRepository.findByUserIdAndChannelId(
                        user.getId(),
                        channel.getId()
                );

        if (existingStatus.isPresent()) {
            throw new IllegalArgumentException(
                    "이미 해당 채널의 읽음 상태가 존재합니다."
            );
        }

        ReadStatus readStatus = new ReadStatus(
                user.getId(),
                channel.getId()
        );

        return readStatusRepository.save(readStatus);
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Objects.requireNonNull(id, "읽음 상태 ID는 필수입니다.");

        return readStatusRepository.findById(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");

        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("읽음 상태 수정 정보는 필수입니다.");
        }

        Objects.requireNonNull(request.id(), "읽음 상태 ID는 필수입니다.");
        Objects.requireNonNull(
                request.lastReadAt(),
                "마지막 읽음 시간은 필수입니다."
        );

        ReadStatus readStatus = readStatusRepository.findById(request.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("읽음 상태를 찾을 수 없습니다.")
                );

        readStatus.updateLastReadAt(request.lastReadAt());

        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "읽음 상태 ID는 필수입니다.");

        readStatusRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("읽음 상태를 찾을 수 없습니다.")
                );

        readStatusRepository.deleteById(id);
    }
}