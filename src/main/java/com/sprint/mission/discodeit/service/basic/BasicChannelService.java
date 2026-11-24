
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
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
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

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelResponse createPublic(CreatePublicChannelRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 생성 요청이 없습니다.");
        }

        validatePublic(request.name(), request.description(), null);

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name().trim(),
                request.description()
        );

        return toResponse(channelRepository.save(channel));
    }

    @Override
    public ChannelResponse createPrivate(CreatePrivateChannelRequest request) {
        if (request == null ||
                request.participantIds() == null ||
                request.participantIds().isEmpty()) {
            throw new IllegalArgumentException(
                    "PRIVATE 채널에는 참여자가 필요합니다."
            );
        }

        if (request.participantIds().contains(null)) {
            throw new IllegalArgumentException(
                    "참여자 ID에 null을 포함할 수 없습니다."
            );
        }

        Set<UUID> participantIds = Set.copyOf(request.participantIds());

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
    public ChannelResponse findById(UUID id) {
        return toResponse(requireChannel(id));
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        requireUser(userId);

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC ||
                                channel.getMemberIds().contains(userId)
                )
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(UpdateChannelRequest request) {
        if (request == null || request.id() == null) {
            throw new IllegalArgumentException(
                    "채널 수정 요청이 올바르지 않습니다."
            );
        }

        Channel channel = requireChannel(request.id());

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException(
                    "PRIVATE 채널은 수정할 수 없습니다."
            );
        }

        String name = request.name() == null
                ? channel.getName()
                : request.name();

        String description = request.description() == null
                ? channel.getDescription()
                : request.description();

        validatePublic(name, description, channel.getId());

        channel.update(name.trim(), description);

        return toResponse(channelRepository.save(channel));
    }

    @Override
    public void delete(UUID id) {
        Channel channel = requireChannel(id);

        for (Message message : messageRepository.findAllByChannelId(id)) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.delete(attachmentId);
            }

            User author = userRepository.findById(message.getUserId());

            if (author != null) {
                author.removeMessage(message.getId());
                userRepository.save(author);
            }

            messageRepository.delete(message.getId());
        }

        for (ReadStatus status : readStatusRepository.findAllByChannelId(id)) {
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
        Channel channel = requireChannel(channelId);
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

        if (readStatusRepository.findByUserIdAndChannelId(
                userId, channelId
        ) == null) {
            readStatusRepository.save(
                    new ReadStatus(userId, channelId, Instant.now())
            );
        }
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = requireChannel(channelId);
        User user = requireUser(userId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException(
                    "참여하지 않은 사용자입니다."
            );
        }

        channel.removeMember(userId);
        user.removeChannel(channelId);

        channelRepository.save(channel);
        userRepository.save(user);

        ReadStatus readStatus =
                readStatusRepository.findByUserIdAndChannelId(
                        userId, channelId
                );

        if (readStatus != null) {
            readStatusRepository.delete(readStatus.getId());
        }
    }

    private Channel requireChannel(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "채널 ID가 필요합니다."
            );
        }

        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new ResourceNotFoundException(
                    "채널을 찾을 수 없습니다."
            );
        }

        return channel;
    }

    private User requireUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 필요합니다."
            );
        }

        User user = userRepository.findById(id);

        if (user == null) {
            throw new ResourceNotFoundException(
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
                    "채널 설명은 null일 수 없습니다."
            );
        }

        String normalizedName = name.trim();

        for (Channel existing : channelRepository.findAll()) {
            if (existing.getType() != ChannelType.PUBLIC) {
                continue;
            }

            if (existing.getId().equals(excludeId)) {
                continue;
            }

            if (existing.getName().equalsIgnoreCase(normalizedName)) {
                throw new IllegalArgumentException(
                        "이미 존재하는 채널 이름입니다."
                );
            }
        }
    }

    private ChannelResponse toResponse(Channel channel) {
        Instant lastMessageAt = messageRepository
                .findAllByChannelId(channel.getId())
                .stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Set<UUID> participantIds =
                channel.getType() == ChannelType.PRIVATE
                        ? Set.copyOf(channel.getMemberIds())
                        : Set.of();

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                participantIds
        );
    }
}
