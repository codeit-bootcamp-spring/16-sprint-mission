package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final UserService userService;
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

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

        Channel channel = channelRepository.save(
                new Channel(request.ownerId(), ChannelType.PUBLIC, request.name(), request.description()));
        owner.addChannel(channel.getId());
        channel.addUser(owner.getId());
        userRepository.save(owner);

        return channelMapper.toDto(channel);
    }

    @Override
    public ChannelDto findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        return channelMapper.toDto(channel);
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
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        if (channel.getType() == ChannelType.PUBLIC) throw new IllegalStateException("Public Channel은 수정할 수 없습니다.");

        requireOwner(channel, request.requesterId(), "채널 수정 권한이 없습니다.");
        channel.update(request.name(), request.description());
        channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));;
        requireOwner(channel, requesterId, "채널 삭제 권한이 없습니다.");
        if (!channel.getMessages().isEmpty()) {
            throw new IllegalArgumentException("메시지가 남아 있어 삭제할 수 없습니다.");
        }

        for (UUID userId : channel.getUsers()) {           // 아래(User) 호출
            userService.removeChannel(userId, id);
        }

        channelRepository.deleteById(id);
    }

    @Override
    public void join(UUID id, UUID userId) {
        Channel channel = findById(id);
        userService.findById(userId);
        channel.addUser(userId);
        userService.addChannel(userId, id);
        channelRepository.save(channel);

    }

    @Override
    public void leave(UUID id, UUID userId) {
        Channel channel = findById(id);
        userService.findById(userId);
        channel.removeUser(userId);
        userService.removeChannel(userId, id);
        channelRepository.save(channel);
    }

    @Override
    public void transferOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId) {
        Channel channel = findById(id);
        requireOwner(channel, currentOwnerId, "소유자만 양도 가능합니다.");
        channel.transferOwnership(newOwnerId);
        channelRepository.save(channel);
    }

    @Override
    public void addMessage(UUID id, UUID messageId) {
        Channel channel = findById(id);
        channel.addMessage(messageId);
        channelRepository.save(channel);
    }

    @Override
    public void removeMessage(UUID id, UUID messageId) {
        Channel channel = findById(id);
        channel.removeMessage(messageId);
        channelRepository.save(channel);
    }

    private void requireOwner(Channel channel, UUID requesterId, String message) {
        if (!channel.isOwner(requesterId)) {
            throw new IllegalArgumentException(message);
        }
    }
}
