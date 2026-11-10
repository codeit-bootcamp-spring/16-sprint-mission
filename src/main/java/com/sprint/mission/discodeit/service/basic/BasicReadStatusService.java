
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        if (request == null || request.userId() == null
                || request.channelId() == null || request.lastReadAt() == null) {
            throw new IllegalArgumentException("필수 정보가 없습니다.");
        }

        boolean userExists = userRepository.getAll().stream()
                .anyMatch(user -> user.getId().equals(request.userId()));

        if (!userExists) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (channelRepository.getById(request.channelId()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (readStatusRepository.findByUserIdAndChannelId(
                request.userId(), request.channelId()).isPresent()) {
            throw new IllegalArgumentException("이미 읽음 상태가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt()
        );

        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.getById(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.getById(request.id());

        if (readStatus == null) {
            throw new IllegalArgumentException("읽음 상태를 찾을 수 없습니다.");
        }

        readStatus.update(request.lastReadAt());
        readStatusRepository.save(readStatus);

        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }
}
