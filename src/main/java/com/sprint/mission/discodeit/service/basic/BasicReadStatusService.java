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
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        // 관련된 유저나 채널이 없으면 예외
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User with id " + request.userId() + " not found");
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel with id " + request.channelId() + " not found");
        }

        // 같은 유저와 채널의 ReadStatus가 이미 있으면 예외
        boolean exists = readStatusRepository.findAllByUserId(request.userId()).stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(request.channelId()));
        if (exists) {
            throw new IllegalArgumentException("ReadStatus already exists for this user and channel");
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId(), request.lastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));

        readStatus.update(request.newLastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("ReadStatus with id " + id + " not found");
        }
        readStatusRepository.deleteById(id);
    }
}