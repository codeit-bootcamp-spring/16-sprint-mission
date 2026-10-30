package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
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
    public ReadStatus create(CreateReadStatusRequest request) {
        if (request == null
                || userRepository.findById(request.userId()) == null
                || channelRepository.findById(request.channelId()) == null) {
            throw new IllegalArgumentException(
                    "사용자 또는 채널이 존재하지 않습니다."
            );
        }

        ReadStatus existing =
                readStatusRepository.findByUserIdAndChannelId(
                        request.userId(),
                        request.channelId()
                );

        if (existing != null) {
            throw new IllegalArgumentException(
                    "이미 읽음 상태가 존재합니다."
            );
        }

        ReadStatus status = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt() == null
                        ? Instant.now()
                        : request.lastReadAt()
        );

        return readStatusRepository.save(status);
    }

    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus status = readStatusRepository.findById(id);

        if (status == null) {
            throw new IllegalArgumentException(
                    "읽음 상태를 찾을 수 없습니다."
            );
        }

        return status;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UpdateReadStatusRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "수정 정보가 없습니다."
            );
        }

        ReadStatus status = findById(request.id());

        if (request.lastReadAt() == null) {
            throw new IllegalArgumentException(
                    "읽은 시간이 없습니다."
            );
        }

        status.update(request.lastReadAt());

        return readStatusRepository.save(status);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        readStatusRepository.delete(id);
    }
}