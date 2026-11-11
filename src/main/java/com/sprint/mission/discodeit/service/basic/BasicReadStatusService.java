package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.validator.CommonValidator;
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
    private final CommonValidator validator;


    @Override
    public ReadStatusDto create(ReadStatusCreateDto dto) {
        validator.validateNotNull(
                dto,
                "읽음 상태 생성 정보는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.userId(),
                "사용자 ID는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.channelId(),
                "채널 ID는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.lastReadAt(),
                "마지막 읽은 시간은 null일 수 없습니다."
        );
        userRepository.findById(dto.userId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );
        channelRepository.findById(dto.channelId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "채널이 존재하지 않습니다."
                        )
                );

        boolean exists = readStatusRepository.findAllByUserId(dto.userId())
                .stream()
                .anyMatch(readStatus ->
                        readStatus.getChannelId().equals(dto.channelId())
                );

        if (exists) {
            throw new IllegalStateException(
                    "해당 사용자와 채널의 읽음 상태가 이미 존재합니다."
            );
        }

        ReadStatus readStatus = new ReadStatus(
                dto.userId(),
                dto.channelId(),
                dto.lastReadAt()
        );

        ReadStatus savedReadStatus =
                readStatusRepository.save(readStatus);

        return toDto(savedReadStatus);
    }

    @Override
    public ReadStatusDto findById(UUID id) {
        validator.validateNotNull(
                id,
                "읽음 상태 ID는 null일 수 없습니다."
        );

        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "읽음 상태가 존재하지 않습니다."
                        )
                );

        return toDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        validator.validateNotNull(
                userId,
                "사용자 ID는 null일 수 없습니다."
        );

        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();

    }

    @Override
    public ReadStatusDto update(ReadStatusUpdateDto dto) {
        validator.validateNotNull(
                dto,
                "읽음 상태 수정 정보는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.id(),
                "읽음 상태 ID는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.lastReadAt(),
                "마지막 읽은 시간은 null일 수 없습니다."
        );

        ReadStatus readStatus = readStatusRepository.findById(dto.id())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "읽음 상태가 존재하지 않습니다."
                        )
                );
        readStatus.update(dto.lastReadAt());

        ReadStatus savedReadStatus =
                readStatusRepository.save(readStatus);

        return toDto(savedReadStatus);
    }

    @Override
    public void delete(UUID id) {
        validator.validateNotNull(
                id,
                "읽음 상태 ID는 null일 수 없습니다."
        );
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "읽음 상태가 존재하지 않습니다."
                        )
                );
        readStatusRepository.delete(readStatus);
    }

    private ReadStatusDto toDto(ReadStatus readStatus) {
        return new ReadStatusDto(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}
