package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusCreateResponse create(ReadStatusCreateRequest createRequest) {
        UUID userId = createRequest.userId();
        UUID channelId = createRequest.channelId();

        // 유저와 채널 존재 여부 확인
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("메세지 확인 기록을 생성에 대한 유저가 존재하지 않습니다."));
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("메세지 확인 기록 생성에 대한 채널이 존재하지 않습니다."));
        user.validateNotDeleted();
        channel.validateNotDeleted();

        // 기존 ReadStatus 존재 여부 확인
        if (this.readStatusRepository.findByChannelIdAndUserId(channelId, userId).isPresent()) {
            throw new IllegalArgumentException("이미 메세지 확인 기록이 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(userId, channelId);
        this.readStatusRepository.save(readStatus);
        return ReadStatusCreateResponse.from(readStatus);
    }

    public ReadStatusFindResponse findById(UUID id) {
        ReadStatus readStatus = this.readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("메세지 확인 기록이 존재하지 않습니다."));
        return ReadStatusFindResponse.from(readStatus);
    }

    public List<ReadStatusFindResponse> findAllByUserId(UUID userId) {
        return this.readStatusRepository.findAllByUserId(userId)
                .stream()
                .map((ReadStatusFindResponse::from))
                .toList();
    }

    public ReadStatusUpdateResponse update(ReadStatusUpdateRequest updateRequest) {
        UUID id = updateRequest.id();
        Instant lastReadTime = updateRequest.lastReadTime();

        ReadStatus readStatus = this.readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("메세지 확인 기록이 존재하지 않습니다."));
        readStatus.update(lastReadTime);

        this.readStatusRepository.update(id, readStatus);
        return ReadStatusUpdateResponse.from(readStatus);
    }

    public void delete(UUID id) {
        ReadStatus readStatus = this.readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("메세지 확인 기록이 존재하지 않습니다."));
        readStatus.validateNotDeleted();

        this.readStatusRepository.delete(id);
    }
}
