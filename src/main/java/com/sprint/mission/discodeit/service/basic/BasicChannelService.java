package com.sprint.mission.discodeit.service.basic;

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

        if (dto.getParticipantIds() != null) {
            for (UUID participantId : dto.getParticipantIds()) {
                findUserEntity(participantId);

                // 중복 참여 방지
                if (!channel.getParticipantIds().contains(participantId)) {
                    channel.joinChannel(participantId);
                }
            }
        }

        Channel savedChannel = channelRepository.save(channel);

        for (UUID participantId : channel.getParticipantIds()) {
            User participant = findUserEntity(participantId);

            participant.addChannelId(savedChannel.getId());
            userRepository.save(participant);

            createReadStatusIfAbsent(participantId, savedChannel.getId());
        }

        return toResponseDto(savedChannel);
    }

    @Override
    public ChannelResponseDto joinChannel(UUID channelId, UUID userId) {
        User user = findUserEntity(userId);
        Channel channel = findChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("비공개 채널에는 임의로 참여할 수 없습니다.");
        }

        channel.joinChannel(userId);
        Channel savedChannel = channelRepository.save(channel);

        user.addChannelId(channelId);
        userRepository.save(user);

        createReadStatusIfAbsent(userId, channelId);

        return toResponseDto(savedChannel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = findUserEntity(userId);
        Channel channel = findChannelEntity(channelId);

        channel.leaveChannel(userId);
        channelRepository.save(channel);

        user.removeChannelId(channelId);
        userRepository.save(user);

        readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .ifPresent(readStatus ->
                        readStatusRepository.deleteById(readStatus.getId()));
    }

    @Override
    public ChannelResponseDto findChannel(UUID channelId) {
        Channel channel = findChannelEntity(channelId);

        return toResponseDto(channel);
    }

    @Override
    public List<ChannelResponseDto> findAccessibleChannelsByUserId(UUID userId) {
        findUserEntity(userId);

        return channelRepository.findAccessibleByUserId(userId).stream()
                .map(this::toResponseDto)
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
        Channel channel = findChannelEntity(channelId);

        List<Message> messages =
                messageRepository.findByChannelId(channelId);

        for (Message message : messages) {
            User sender = findUserEntity(message.getSenderId());

            sender.removeMessageId(message.getId());
            userRepository.save(sender);

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

        for (UUID participantId : channel.getParticipantIds()) {
            User participant = findUserEntity(participantId);

            participant.removeChannelId(channelId);
            userRepository.save(participant);
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
                    ReadStatus readStatus = new ReadStatus(userId, channelId);

                    return readStatusRepository.save(readStatus);
                });
    }

    private ChannelResponseDto toResponseDto(Channel channel) {
        Instant lastMessageAt = messageRepository
                .findLatestCreatedAtByChannelId(channel.getId())
                .orElse(null);

        return ChannelResponseDto.builder()
                .id(channel.getId())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .lastMessageAt(lastMessageAt)
                .participantIds(channel.getParticipantIds())
                .build();
    }
}