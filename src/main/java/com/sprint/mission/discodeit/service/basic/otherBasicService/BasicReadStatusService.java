package com.sprint.mission.discodeit.service.basic.otherBasicService;

import com.sprint.mission.discodeit.dto.request.status.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.status.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.other.ReadStatusService;
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

        // 유저 존재 여부 확인
        userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 채널 존재 여부 확인
        channelRepository.findByChannelId(request.channelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        // 동일한 유저와 채널의 읽음 상태 중복 확인
        readStatusRepository.findByUserIdAndChannelId(
                request.userId(), request.channelId()
        ).ifPresent(readStatus -> {
            throw new IllegalArgumentException("이미 존재하는 읽음 상태입니다.");
        });

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt()
        );

        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("읽음 상태를 찾을 수 없습니다."));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {

        ReadStatus readStatus = find(request.id());

        readStatus.updateLastReadAt(request.lastReadAt());

        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        find(id);
        readStatusRepository.deleteById(id);
    }
}