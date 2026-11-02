package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

        userRepository.findById(request.userId()).orElseThrow(()->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        channelRepository.findById(request.channelId()).orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));
        for(ReadStatus readStatus : readStatusRepository.findAll()){
            if(readStatus.getChannelId().equals(request.channelId())&&readStatus.getUserId().equals(request.userId())){
                throw new IllegalArgumentException("중복조회생성 불가");
            }
        }
        ReadStatus readStatus = new ReadStatus(request.userId(),request.channelId(), request.lastReadAt());
        readStatusRepository.save(readStatus);

        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        return readStatusRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("조회상태를 찾을 수 없음"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAll();
        return readStatuses.stream().filter(m->m.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.readStatusId()).orElseThrow(()->
                new IllegalArgumentException("조회상태를 찾을 수 없음"));

        readStatus.update(request.lastReadAt());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("조회상태를 찾을 수 없음"));

        readStatusRepository.deleteById(id);
    }
}
