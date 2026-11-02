package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
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

    // 읽음 상태 생성
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException(
                    "User not found with id " + request.getUserId()
            );
        }

        // 채널 존재 여부 확인
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException(
                    "Channel not found with id " + request.getChannelId()
            );
        }

        // 이미 해당 사용자의 읽음 상태가 있는지 확인
        if (readStatusRepository.findByUserIdAndChannelId(
                request.getUserId(),
                request.getChannelId()
        ).isPresent()) {
            throw new IllegalArgumentException(
                    "ReadStatus already exists"
            );
        }

        ReadStatus readStatus = new ReadStatus(
                request.getUserId(),
                request.getChannelId()
        );

        readStatusRepository.save(readStatus);

        return toDto(readStatus);
    }

    // 읽음 상태 단건 조회
    @Override
    public ReadStatusDto find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException(
                        "ReadStatus not found with id " + readStatusId
                ));

        return toDto(readStatus);
    }

    // 전체 읽음 상태 조회
    @Override
    public List<ReadStatusDto> findAll() {
        return readStatusRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // 특정 사용자의 읽음 상태 조회
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(
                    "User not found with id " + userId
            );
        }

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    // 읽음 상태 수정
    @Override
    public ReadStatusDto update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository
                .findById(request.getReadStatusId())
                .orElseThrow(() -> new NoSuchElementException(
                        "ReadStatus not found with id "
                                + request.getReadStatusId()
                ));

        readStatus.update(request.getLastReadAt());

        readStatusRepository.save(readStatus);

        return toDto(readStatus);
    }

    // 읽음 상태 삭제
    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException(
                    "ReadStatus not found with id " + readStatusId
            );
        }

        readStatusRepository.deleteById(readStatusId);
    }

    // Entity를 DTO로 변환
    private ReadStatusDto toDto(ReadStatus readStatus) {
        return new ReadStatusDto(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}
