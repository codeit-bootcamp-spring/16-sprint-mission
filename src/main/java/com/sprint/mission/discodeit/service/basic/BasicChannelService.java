package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
        Set<UUID> memberIds = new LinkedHashSet<>(request.participantIds());
        memberIds.add(request.ownerId());                       // owner도 참여자에 포함

        List<User> members = memberIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id)))
                .toList();

        Channel channel = new Channel(request.ownerId(), ChannelType.PRIVATE, null, null);
        memberIds.forEach(channel::addUser);
        channelRepository.save(channel);

        for (User member : members) {
            member.addChannel(channel.getId());
            userRepository.save(member);
            readStatusRepository.save(new ReadStatus(member.getId(), channel.getId(), Instant.now()));
        }

        return channelMapper.toDto(channel);
    }

    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + request.ownerId()));

        Channel channel = new Channel(request.ownerId(), ChannelType.PUBLIC, request.name(), request.description());
        owner.addChannel(channel.getId());
        channelRepository.save(channel);

        channel.addUser(owner.getId());
        userRepository.save(owner);

        return channelMapper.toDto(channel);
    }

    @Override
    public ChannelDto findById(UUID id) {
        Channel channel = getChannelOrThrow(id);
        return channelMapper.toDto(channel);
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC
                || channel.hasUser(userId))
                .map(channelMapper::toDto)
                .toList();

    }

    @Override
    public ChannelDto update(UUID id, ChannelUpdateRequest request) {
        Channel channel = getChannelOrThrow(id);
        if (channel.getType() == ChannelType.PRIVATE) throw new IllegalStateException("Private Channel은 수정할 수 없습니다.");

        requireOwner(channel, request.requesterId(), "채널 수정 권한이 없습니다.");
        channel.update(request.name(), request.description());
        channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Channel channel = getChannelOrThrow(id);
        requireOwner(channel, requesterId, "채널 삭제 권한이 없습니다.");

        List<Message> messages = messageRepository.findAllByChannelId(id);
        for (Message message : messages) {
            message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
        }
        messageRepository.deleteAllByChannelId(id);

        readStatusRepository.deleteAllByChannelId(id);

        for (UUID userId : channel.getUsers()) {
            userRepository.findById(userId).ifPresent(user -> {
                user.removeChannel(id);
                messages.stream()
                        .filter(m -> m.getAuthorId().equals(userId))
                        .forEach(m -> user.removeMessage(m.getId()));
                userRepository.save(user);
            });
        }

        channelRepository.deleteById(id);
    }

    @Override
    public void join(UUID id, UUID userId) {
        Channel channel = getChannelOrThrow(id);
        if (channel.getType() == ChannelType.PRIVATE)
            throw new IllegalStateException("PRIVATE 채널에는 참여할 수 없습니다.");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        channel.addUser(userId);
        user.addChannel(id);
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leave(UUID id, UUID userId) {
        Channel channel = getChannelOrThrow(id);
        if (channel.getOwnerId().equals(userId))
            throw new IllegalStateException("소유자는 채널을 나갈 수 없습니다. 먼저 소유권을 양도하세요.");

        channel.removeUser(userId);
        channelRepository.save(channel);
        userRepository.findById(userId).ifPresent(user -> {
            user.removeChannel(id);
            userRepository.save(user);
        });
        readStatusRepository.findByUserIdAndChannelId(userId, id)
                .ifPresent(rs -> readStatusRepository.deleteById(rs.getId()));
    }

    @Override
    public void transferOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId) {
        Channel channel = getChannelOrThrow(id);
        requireOwner(channel, currentOwnerId, "소유자만 양도 가능합니다.");

        if (!channel.hasUser(newOwnerId))
            throw new IllegalArgumentException("채널 멤버에게만 소유권을 양도할 수 있습니다.");

        channel.transferOwnership(newOwnerId);
        channelRepository.save(channel);
    }

    private void requireOwner(Channel channel, UUID requesterId, String message) {
        if (!channel.isOwner(requesterId)) {
            throw new IllegalArgumentException(message);
        }
    }
}
