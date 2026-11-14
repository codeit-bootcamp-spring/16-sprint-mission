package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponseDto createReadStatus(ReadStatusCreateRequestDto dto) {
        UUID userId = dto.userId();
        UUID channelId = dto.channelId();

        findUserEntity(userId);
        Channel channel = findChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE
                && readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .isEmpty()
        ) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_PARTICIPANT);
        }

        if (readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .isPresent()
        ) {
            throw new BusinessLogicException(ExceptionCode.READ_STATUS_ALREADY_EXISTS);
        }

        ReadStatus readStatus = new ReadStatus(
                userId,
                channelId,
                dto.lastReadAt()
        );

        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);

        return toResponseDto(savedReadStatus);
    }

    @Override
    public ReadStatusResponseDto findReadStatus(UUID readStatusId) {
        ReadStatus readStatus = findReadStatusEntity(readStatusId);

        return toResponseDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllReadStatusesByUserId(UUID userId) {
        findUserEntity(userId);

        return readStatusRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public ReadStatusResponseDto updateReadStatus(
            UUID readStatusId,
            ReadStatusUpdateRequestDto dto
    ) {
        ReadStatus readStatus = findReadStatusEntity(readStatusId);

        readStatus.updateLastReadAt(dto.newLastReadAt());

        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);

        return toResponseDto(savedReadStatus);
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        findReadStatusEntity(readStatusId);

        readStatusRepository.deleteById(readStatusId);
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    private Channel findChannelEntity(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    }

    private ReadStatus findReadStatusEntity(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND));
    }

    private ReadStatusResponseDto toResponseDto(ReadStatus readStatus) {
        return ReadStatusResponseDto.builder()
                .id(readStatus.getId())
                .createdAt(readStatus.getCreatedAt())
                .updatedAt(readStatus.getUpdatedAt())
                .userId(readStatus.getUserId())
                .channelId(readStatus.getChannelId())
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }
}
