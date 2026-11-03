package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelResponse createPublic(
            CreatePublicChannelRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "채널 생성 정보가 없습니다."
            );
        }

        validatePublicChannel(
                request.name(),
                request.description(),
                null
        );

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name().trim(),
                request.description()
        );

        Channel savedChannel =
                channelRepository.save(channel);

        return toResponse(savedChannel);
    }

    @Override
    public ChannelResponse createPrivate(
            CreatePrivateChannelRequest request
    ) {
        if (request == null
                || request.participantIds() == null
                || request.participantIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "PRIVATE 채널에는 참여자가 필요합니다."
            );
        }

        Set<UUID> participantIds =
                Set.copyOf(request.participantIds());

        for (UUID userId : participantIds) {
            requireUser(userId);
        }

        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );

        Channel savedChannel =
                channelRepository.save(channel);

        for (UUID userId : participantIds) {
            ReadStatus readStatus = new ReadStatus(
                    userId,
                    savedChannel.getId(),
                    Instant.now()
            );

            readStatusRepository.save(readStatus);

            User user = requireUser(userId);
            user.addChannel(savedChannel.getId());
            userRepository.save(user);
        }

        return toResponse(savedChannel);
    }

    @Override
    public ChannelResponse findById(UUID id) {
        Channel channel = requireChannel(id);
        return toResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        requireUser(userId);

        Set<UUID> privateChannelIds =
                readStatusRepository.findAllByUserId(userId)
                        .stream()
                        .map(ReadStatus::getChannelId)
                        .collect(Collectors.toSet());

        return channelRepository.findAll()
                .stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || privateChannelIds.contains(
                                channel.getId()
                        )
                )
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(UpdateChannelRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "채널 수정 정보가 없습니다."
            );
        }

        Channel channel = requireChannel(request.id());

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException(
                    "PRIVATE 채널은 수정할 수 없습니다."
            );
        }

        validatePublicChannel(
                request.name(),
                request.description(),
                channel.getId()
        );

        channel.update(
                request.name().trim(),
                request.description()
        );

        Channel updatedChannel =
                channelRepository.save(channel);

        return toResponse(updatedChannel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = requireChannel(id);

        List<Message> messages =
                messageRepository.findAllByChannelId(id);

        for (Message message : messages) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.delete(attachmentId);
            }

            User author =
                    userRepository.findById(message.getUserId());

            if (author != null) {
                author.removeMessage(message.getId());
                userRepository.save(author);
            }

            messageRepository.delete(message.getId());
        }

        List<ReadStatus> readStatuses =
                readStatusRepository.findAllByChannelId(id);

        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.delete(readStatus.getId());
        }

        for (User user : userRepository.findAll()) {
            if (user.getChannelIds().contains(id)) {
                user.removeChannel(id);
                userRepository.save(user);
            }
        }

        channelRepository.delete(channel.getId());
    }

    @Override
    public void joinChannel(
            UUID channelId,
            UUID userId
    ) {
        Channel channel = requireChannel(channelId);
        User user = requireUser(userId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException(
                    "PRIVATE 채널의 참여자는 생성 시 지정해야 합니다."
            );
        }

        ReadStatus existing =
                readStatusRepository.findByUserIdAndChannelId(
                        userId,
                        channelId
                );

        if (existing != null) {
            throw new IllegalArgumentException(
                    "이미 채널에 참여 중입니다."
            );
        }

        channel.addMember(userId);
        user.addChannel(channelId);

        channelRepository.save(channel);
        userRepository.save(user);

        ReadStatus readStatus = new ReadStatus(
                userId,
                channelId,
                Instant.now()
        );

        readStatusRepository.save(readStatus);
    }

    @Override
    public void leaveChannel(
            UUID channelId,
            UUID userId
    ) {
        Channel channel = requireChannel(channelId);
        User user = requireUser(userId);

        ReadStatus readStatus =
                readStatusRepository.findByUserIdAndChannelId(
                        userId,
                        channelId
                );

        if (readStatus == null) {
            throw new IllegalArgumentException(
                    "참여하지 않은 채널입니다."
            );
        }

        readStatusRepository.delete(readStatus.getId());

        if (channel.getMemberIds().contains(userId)) {
            channel.removeMember(userId);
            channelRepository.save(channel);
        }

        user.removeChannel(channelId);
        userRepository.save(user);
    }

    private ChannelResponse toResponse(Channel channel) {
        Instant lastMessageAt =
                messageRepository.findAllByChannelId(channel.getId())
                        .stream()
                        .map(Message::getCreatedAt)
                        .max(Comparator.naturalOrder())
                        .orElse(null);

        Set<UUID> participantIds;

        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds =
                    readStatusRepository.findAllByChannelId(
                                    channel.getId()
                            )
                            .stream()
                            .map(ReadStatus::getUserId)
                            .collect(Collectors.toUnmodifiableSet());
        } else {
            participantIds = Set.of();
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                participantIds
        );
    }

    private Channel requireChannel(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "채널 ID가 없습니다."
            );
        }

        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException(
                    "채널을 찾을 수 없습니다."
            );
        }

        return channel;
    }

    private User requireUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 없습니다."
            );
        }

        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    private void validatePublicChannel(
            String name,
            String description,
            UUID excludeId
    ) {
        if (name == null
                || name.isBlank()
                || description == null) {

            throw new IllegalArgumentException(
                    "채널 이름과 설명을 확인하세요."
            );
        }

        for (Channel channel : channelRepository.findAll()) {
            if (channel.getId().equals(excludeId)) {
                continue;
            }

            if (channel.getType() == ChannelType.PUBLIC
                    && channel.getName()
                    .equalsIgnoreCase(name.trim())) {

                throw new IllegalArgumentException(
                        "이미 사용 중인 채널 이름입니다."
                );
            }
        }
    }
}