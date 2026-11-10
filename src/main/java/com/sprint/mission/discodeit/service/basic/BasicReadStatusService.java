package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.status.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.status.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;
import com.sprint.mission.discodeit.exceptions.DuplicateResourceException;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.otherService.ReadStatusService;
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
    public ReadStatus create(ReadStatusCreateRequest request) {

        // 유저 존재 여부 확인
        userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 채널 존재 여부 확인
        channelRepository.findByChannelId(request.channelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("채널을 찾을 수 없습니다."));

        // 동일한 유저와 채널의 읽음 상태 중복 확인
        readStatusRepository.findByUserIdAndChannelId(
                request.userId(), request.channelId()
        ).ifPresent(readStatus -> {
            throw new DuplicateResourceException("이미 존재하는 읽음 상태입니다.");
        });

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                Instant.now()
        );

        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("읽음 상태를 찾을 수 없습니다."));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {

        // 유저 존재 여부 확인
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {

        ReadStatus readStatus = find(readStatusId);

        readStatus.updateLastReadAt(request.lastReadAt());

        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        find(id);
        readStatusRepository.deleteById(id);
    }
}