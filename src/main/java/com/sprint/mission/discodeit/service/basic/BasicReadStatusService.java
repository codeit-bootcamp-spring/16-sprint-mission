package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.dto.response.ReadStatusResponse.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(CreateReadStatusRequest request) {
        if (request.userId() == null || request.channelId() == null) {
            throw new IllegalArgumentException("유효하지 않은 아이디입니다.");
        }

        if (!channelRepository.existsById(request.channelId()) || !userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("존재하지 않는 채널 또는 유저입니다.");
        }

        boolean isDuplicated = readStatusRepository.findAll().stream()
                .anyMatch(status ->
                        request.userId().equals(status.getUserId()) && request.channelId().equals(status.getChannelId()));

        if (isDuplicated) {
            throw new IllegalStateException("ReadStatus가 중복됨");
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);

        return from(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID statusId) {
        ReadStatus readStatus = readStatusRepository.findById(statusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 read status가 존재하지 않음"));

        return from(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 id의 유저는 존재하지 않음");
        }

        return readStatusRepository.findAll().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .map(ReadStatusResponse::from)
                .toList();
    }

    @Override
    public ReadStatusResponse update(UpdateReadStatusRequest request) {
        if (request.lastReadAt() == null) {
            throw new IllegalArgumentException("최근 읽은 시간이 null일 수 없음");
        }

        ReadStatus readStatus = readStatusRepository.findById(request.statusId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 read status는 존재하지 않음"));

        if (request.lastReadAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException("최근 읽은 시간이 현재보다 이후일 수 없음");
        }

        readStatus.update(request.lastReadAt());
        readStatusRepository.save(readStatus);
        return from(readStatus);
    }

    @Override
    public void delete(UUID statusId) {
        if (!readStatusRepository.existsById(statusId)) {
            throw new NoSuchElementException("해당 id의 read status는 존재하지 않음");
        }

        readStatusRepository.deleteById(statusId);
    }
}
