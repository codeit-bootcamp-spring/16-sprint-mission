package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelResponseDto createPublicChannel(
            PublicChannelCreateRequestDto dto
    ) {
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                dto.getName(),
                dto.getDescription()
        );

        Channel savedChannel = channelRepository.save(channel);

        return toResponseDto(savedChannel);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(
            PrivateChannelCreateRequestDto dto
    ) {
        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );

        Channel savedChannel = channelRepository.save(channel);

        if (dto.getParticipantIds() != null) {
            for (UUID participantId : dto.getParticipantIds()) {
                findUserEntity(participantId);

                createReadStatusIfAbsent(
                        participantId,
                        savedChannel.getId()
                );
            }
        }

        return toResponseDto(savedChannel);
    }

    @Override
    public ChannelResponseDto joinChannel(UUID channelId, UUID userId) {
        findUserEntity(userId);
        Channel channel = findChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("비공개 채널에는 임의로 참여할 수 없습니다.");
        }

        // PUBLIC 채널은 모든 사용자가 참여자로 간주되므로
        // 별도의 참여 정보 저장이 필요하지 않음

        return toResponseDto(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        findUserEntity(userId);
        Channel channel = findChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                    .ifPresent(readStatus ->
                            readStatusRepository.deleteById(readStatus.getId()));
        }

        // PUBLIC 채널은 모든 사용자가 참여자로 간주되므로
        // 별도의 참여 정보 삭제가 필요하지 않음
    }

    @Override
    public ChannelResponseDto findChannel(UUID channelId) {
        Channel channel = findChannelEntity(channelId);

        return toResponseDto(channel);
    }

    @Override
    public List<ChannelDto> findAccessibleChannelsByUserId(UUID userId) {
        findUserEntity(userId);

        List<Channel> publicChannels = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .toList();

        List<Channel> accessibleChannels = new ArrayList<>(publicChannels);

        List<ReadStatus> readStatuses =
                readStatusRepository.findByUserId(userId);

        for (ReadStatus readStatus : readStatuses) {
            Channel channel = channelRepository
                    .findById(readStatus.getChannelId())
                    .orElse(null);

            if (channel != null
                    && channel.getType() == ChannelType.PRIVATE) {
                accessibleChannels.add(channel);
            }
        }

        return accessibleChannels.stream()
                .map(this::toChannelDto)
                .toList();
    }

    @Override
    public ChannelResponseDto updateChannel(
            UUID channelId,
            ChannelUpdateRequestDto dto
    ) {
        Channel channel = findChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new BusinessLogicException(
                    ExceptionCode.PRIVATE_CHANNEL_CANNOT_BE_UPDATED
            );
        }

        if (dto.getNewName() != null) {
            channel.changeName(dto.getNewName());
        }

        if (dto.getNewDescription() != null) {
            channel.changeDescription(dto.getNewDescription());
        }

        Channel savedChannel = channelRepository.save(channel);

        return toResponseDto(savedChannel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelEntity(channelId);

        List<Message> messages =
                messageRepository.findByChannelId(channelId);

        for (Message message : messages) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }

            messageRepository.deleteById(message.getId());
        }

        List<ReadStatus> readStatuses =
                readStatusRepository.findByChannelId(channelId);

        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.deleteById(readStatus.getId());
        }

        channelRepository.deleteById(channelId);
    }

    private Channel findChannelEntity(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessLogicException(
                        ExceptionCode.CHANNEL_NOT_FOUND,
                        channelId
                ));
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(
                        ExceptionCode.USER_NOT_FOUND,
                        userId
                ));
    }

    private void createReadStatusIfAbsent(
            UUID userId,
            UUID channelId
    ) {
        readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .orElseGet(() -> {
                    ReadStatus readStatus =
                            new ReadStatus(userId, channelId, null);

                    return readStatusRepository.save(readStatus);
                });
    }

    private ChannelResponseDto toResponseDto(Channel channel) {
        return ChannelResponseDto.builder()
                .id(channel.getId())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .build();
    }

    private ChannelDto toChannelDto(Channel channel) {
        Instant lastMessageAt = messageRepository
                .findLatestCreatedAtByChannelId(channel.getId())
                .orElse(null);

        return ChannelDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .participantIds(findParticipantIds(channel))
                .lastMessageAt(lastMessageAt)
                .build();
    }

    private List<UUID> findParticipantIds(Channel channel) {
        if (channel.getType() == ChannelType.PUBLIC) {
            return userRepository.findAll().stream()
                    .map(User::getId)
                    .toList();
        }

        return readStatusRepository.findByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUserId)
                .toList();
    }
}
