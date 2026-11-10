package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ConflictException;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        userRepository.findById(request.userId()).orElseThrow(() ->
                new ResourceNotFoundException("유저를 찾을 수 없음"));

        channelRepository.findById(request.channelId()).orElseThrow(() ->
                new ResourceNotFoundException("채널을 찾을 수 없음"));

        for (ReadStatus readStatus : readStatusRepository.findAll()) {
            if (readStatus.getChannelId().equals(request.channelId())
                    && readStatus.getUserId().equals(request.userId())) {
                throw new ConflictException("이미 등록된 메시지 수신 정보");
            }
        }

        ReadStatus readStatus = new ReadStatus(
                request.userId(), request.channelId(), request.lastReadAt());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return readStatusRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("조회상태를 찾을 수 없음"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAll();
        return readStatuses.stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = findById(request.readStatusId());

        readStatus.update(request.lastReadAt());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        readStatusRepository.deleteById(id);
    }
}