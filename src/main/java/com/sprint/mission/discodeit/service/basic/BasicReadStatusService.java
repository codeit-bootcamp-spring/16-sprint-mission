package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
            throw new IllegalArgumentException("읽음 상태 생성 요청은 null일 수 없습니다.");
        }
        if (request.userId() == null || request.channelId() == null || request.lastReadAt() == null) {
            throw new IllegalArgumentException("사용자, 채널, 마지막 읽은 시각은 필수입니다.");
        }

        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "사용자를 찾을 수 없습니다: " + request.userId()));
        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "채널을 찾을 수 없습니다: " + request.channelId()));

        if (readStatusRepository.findByUserIdAndChannelId(
                request.userId(), request.channelId()).isPresent()) {
            throw new IllegalArgumentException("해당 사용자의 채널 읽음 상태가 이미 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                request.userId(), request.channelId(), request.lastReadAt()
        );
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "읽음 상태를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void update(ReadStatusUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("읽음 상태 수정 요청은 null일 수 없습니다.");
        }
        if (request.id() == null || request.lastReadAt() == null) {
            throw new IllegalArgumentException("읽음 상태 ID와 마지막 읽은 시각은 필수입니다.");
        }

        ReadStatus readStatus = findById(request.id());
        readStatus.updateLastReadAt(request.lastReadAt());
        readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        if (!readStatusRepository.deleteById(id)) {
            throw new IllegalArgumentException("읽음 상태를 찾을 수 없습니다: " + id);
        }
    }
}
