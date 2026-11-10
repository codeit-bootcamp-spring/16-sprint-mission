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

    // 공개 채널 생성
    @Override
    public ChannelResponseDto createPublicChannel(
            UUID creatorId,
            PublicChannelCreateRequestDto dto
    ) {
        User creator = findUserEntity(creatorId);

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                dto.getChannelName(),
                dto.getDescription(),
                creatorId
        );

        channel.joinChannel(creatorId);
        Channel savedChannel = channelRepository.save(channel);

        creator.addChannelId(savedChannel.getId());
        userRepository.save(creator);

        createReadStatusIfAbsent(creatorId, savedChannel.getId());

        return toResponseDto(savedChannel);
    }

    // 비공개 채널 생성
    @Override
    public ChannelResponseDto createPrivateChannel(
            UUID creatorId,
            PrivateChannelCreateRequestDto dto
    ) {
        findUserEntity(creatorId);

        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null,
                creatorId
        );

        channel.joinChannel(creatorId);

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

    // 사용자가 볼 수 있는 채널 목록 조회
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

        // 비공개 채널은 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException(
                    "비공개 채널은 수정할 수 없습니다."
            );
        }

        if (dto.getChannelName() != null) {
            channel.changeChannelName(dto.getChannelName());
        }

        if (dto.getDescription() != null) {
            channel.changeDescription(dto.getDescription());
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
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다: " + channelId));
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
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
        List<Message> messages =
                messageRepository.findByChannelId(channel.getId());

        Instant lastMessageAt = messages.stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        return ChannelResponseDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .channelName(channel.getChannelName())
                .description(channel.getDescription())
                .creatorId(channel.getCreatorId())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .lastMessageAt(lastMessageAt)
                .participantIds(channel.getParticipantIds())
                .build();
    }
}