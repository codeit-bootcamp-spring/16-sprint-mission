package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public Channel add(Channel channel) {
        if (channelRepository.getById(channel.getId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel getById(UUID id) {
        return channelRepository.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.getAll();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = getById(id);
        channel.update(name);
        channelRepository.save(channel);
        return channel;
    }


    @Override
    public void delete(UUID id) {
        List<UUID> attachmentIds = messageRepository.getAll().stream()
                .filter(message -> id.equals(message.getChannelId()))
                .flatMap(message -> message.getAttachmentIds().stream())
                .distinct()
                .toList();
        channelRepository.getById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다.")
                );

        for (Message message : messageRepository.getAll()) {
            if (id.equals(message.getChannelId())) {
                messageRepository.delete(message.getId());
            }
        }

        for (ReadStatus readStatus : readStatusRepository.findAllByChannelId(id)) {
            readStatusRepository.delete(readStatus.getId());
        }
        for (UUID attachmentId : attachmentIds) {
            binaryContentRepository.delete(attachmentId);
        }

        channelRepository.delete(id);
    }

    @Override
    public ChannelResponse createPublic(PublicChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 생성 정보가 필요합니다.");
        }

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name(),
                request.description()
        );

        channelRepository.save(channel);

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                List.of()
        );

    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        if (request == null || request.id() == null) {
            throw new IllegalArgumentException("수정할 채널 ID가 필요합니다.");
        }

        Channel channel = getById(request.id());

        channel.update(request.name(), request.description());
        channelRepository.save(channel);

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                List.of()
        );
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        if (request == null
                || request.userIds() == null
                || request.userIds().isEmpty()) {
            throw new IllegalArgumentException("참여 사용자가 필요합니다.");
        }

        Set<UUID> uniqueUserIds = new HashSet<>();

        for (UUID userId : request.userIds()) {
            if (userId == null || !uniqueUserIds.add(userId)) {
                throw new IllegalArgumentException("유효하지 않거나 중복된 사용자입니다.");
            }

            if (userRepository.getById(userId) == null) {
                throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
            }
        }

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        Instant now = Instant.now();

        for (UUID userId : request.userIds()) {
            ReadStatus readStatus = new ReadStatus(
                    userId,
                    channel.getId(),
                    now
            );

            readStatusRepository.save(readStatus);
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                List.copyOf(request.userIds())
        );
    }

    @Override
    public ChannelResponse find(UUID id) {
        Channel channel = getById(id);

        Instant latestMessageCreatedAt = messageRepository.getAll().stream()
                .filter(message -> id.equals(message.getChannelId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantUserIds = List.of();

        if (channel.getType() == ChannelType.PRIVATE) {
            participantUserIds = readStatusRepository.findAllByChannelId(id).stream()
                    .map(ReadStatus::getUserId)
                    .distinct()
                    .toList();
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                latestMessageCreatedAt,
                participantUserIds
        );
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID가 필요합니다.");
        }

        return channelRepository.getAll().stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    }

                    return readStatusRepository
                            .findAllByChannelId(channel.getId())
                            .stream()
                            .anyMatch(readStatus ->
                                    userId.equals(readStatus.getUserId()));
                })
                .map(channel -> find(channel.getId()))
                .toList();
    }




}