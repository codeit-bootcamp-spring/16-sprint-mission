package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Channel create(String name, String description) {
        return create(ChannelType.PUBLIC, name, description);
    }

    @Override
    public Channel create(
            ChannelType type,
            String name,
            String description
    ) {
        if (type != ChannelType.PUBLIC) {
            throw new IllegalArgumentException(
                    "PRIVATE 채널은 참여자 DTO로 생성해야 합니다."
            );
        }

        return createPublicInternal(
                new CreatePublicChannelRequest(name, description)
        );
    }

    @Override
    public ChannelResponse createPublic(
            CreatePublicChannelRequest request
    ) {
        return toResponse(createPublicInternal(request));
    }

    private Channel createPublicInternal(
            CreatePublicChannelRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("채널 정보가 없습니다.");
        }

        validatePublic(
                request.name(),
                request.description(),
                null
        );

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name().trim(),
                request.description()
        );

        return channelRepository.save(channel);
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

        for (UUID userId : participantIds) {
            channel.addMember(userId);
        }

        channelRepository.save(channel);

        for (UUID userId : participantIds) {
            User user = requireUser(userId);

            user.addChannel(channel.getId());
            userRepository.save(user);

            readStatusRepository.save(
                    new ReadStatus(
                            userId,
                            channel.getId(),
                            Instant.now()
                    )
            );
        }

        return toResponse(channel);
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException(
                    "채널을 찾을 수 없습니다."
            );
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public ChannelResponse findResponseById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        requireUser(userId);

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || channel.getMemberIds().contains(userId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public Channel update(
            UUID id,
            String name,
            String description
    ) {
        updateInternal(
                new UpdateChannelRequest(id, name, description)
        );

        return findById(id);
    }

    @Override
    public ChannelResponse update(
            UpdateChannelRequest request
    ) {
        return toResponse(updateInternal(request));
    }

    private Channel updateInternal(
            UpdateChannelRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "채널 수정 정보가 없습니다."
            );
        }

        Channel channel = findById(request.id());

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException(
                    "PRIVATE 채널은 수정할 수 없습니다."
            );
        }

        validatePublic(
                request.name(),
                request.description(),
                channel.getId()
        );

        channel.update(
                request.name().trim(),
                request.description()
        );

        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);

        for (Message message :
                messageRepository.findAllByChannelId(id)) {

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

        for (ReadStatus status :
                readStatusRepository.findAllByChannelId(id)) {
            readStatusRepository.delete(status.getId());
        }

        for (UUID memberId : channel.getMemberIds()) {
            User member = userRepository.findById(memberId);

            if (member != null) {
                member.removeChannel(id);
                userRepository.save(member);
            }
        }

        channelRepository.delete(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = requireUser(userId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException(
                    "PRIVATE 채널은 생성 시 참여자를 지정합니다."
            );
        }

        if (channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException(
                    "이미 참여 중인 사용자입니다."
            );
        }

        channel.addMember(userId);
        user.addChannel(channelId);

        channelRepository.save(channel);
        userRepository.save(user);

        ReadStatus readStatus =
                readStatusRepository.findByUserIdAndChannelId(
                        userId,
                        channelId
                );

        if (readStatus == null) {
            readStatusRepository.save(
                    new ReadStatus(
                            userId,
                            channelId,
                            Instant.now()
                    )
            );
        }
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = requireUser(userId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException(
                    "참여하지 않은 사용자입니다."
            );
        }

        for (UUID messageId : user.getMessageIds()) {
            if (channel.getMessageIds().contains(messageId)) {
                throw new IllegalStateException(
                        "작성한 메시지가 남아 있어 탈퇴할 수 없습니다."
                );
            }
        }

        channel.removeMember(userId);
        user.removeChannel(channelId);

        channelRepository.save(channel);
        userRepository.save(user);

        ReadStatus readStatus =
                readStatusRepository.findByUserIdAndChannelId(
                        userId,
                        channelId
                );

        if (readStatus != null) {
            readStatusRepository.delete(readStatus.getId());
        }
    }

    private ChannelResponse toResponse(Channel channel) {
        Instant lastMessageAt =
                messageRepository.findAllByChannelId(channel.getId())
                        .stream()
                        .map(Message::getCreatedAt)
                        .max(Comparator.naturalOrder())
                        .orElse(null);

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                channel.getType() == ChannelType.PRIVATE
                        ? Set.copyOf(channel.getMemberIds())
                        : Set.of()
        );
    }

    private User requireUser(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    private void validatePublic(
            String name,
            String description,
            UUID excludeId
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "채널 이름을 입력하세요."
            );
        }

        if (description == null) {
            throw new IllegalArgumentException(
                    "채널 설명을 입력하세요."
            );
        }

        for (Channel channel : channelRepository.findAll()) {
            if (!channel.getId().equals(excludeId)
                    && channel.getType() == ChannelType.PUBLIC
                    && channel.getName().equalsIgnoreCase(name.trim())) {
                throw new IllegalArgumentException(
                        "이미 존재하는 채널 이름입니다."
                );
            }
        }
    }
}