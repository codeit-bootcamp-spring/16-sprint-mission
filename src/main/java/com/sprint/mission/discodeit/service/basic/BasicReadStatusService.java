package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor // @RequiredArgsConstructor는 초기화되지 않은 final 필드 등을 대상으로 생성자를 만들어줍니다.
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        if (!this.userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User with id " + request.userId() + " not found.");
        }

        if (!this.channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel with id " + request.channelId() + " not found.");
        }

        if (this.readStatusRepository.findAll().stream()
                .anyMatch(status ->
                        status.getUserId().equals(request.userId())
                                && status.getChannelId().equals(request.channelId()))) {
            throw new IllegalStateException("ReadStatus already exists for user " + request.userId()
                    + " and channel " + request.channelId());
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = this.readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found."));

        return this.toReadStatusResponse(readStatus);
    }

    private ReadStatusResponse toReadStatusResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found.");
        }

        List<ReadStatus> readStatus = this.readStatusRepository.findAllByUserId(userId);

        return readStatus.stream()
                .map(this::toReadStatusResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = this.readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found."));

        readStatus.update();

        this.readStatusRepository.save(readStatus);
        return this.toReadStatusResponse(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!this.readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found.");
        }
        this.readStatusRepository.deleteById(readStatusId);
    }
}
