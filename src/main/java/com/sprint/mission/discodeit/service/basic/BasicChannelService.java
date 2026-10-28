package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final UserService userService;
    private final ChannelRepository channelRepository;

    public BasicChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(Channel channel) {
        userService.findById(channel.getOwnerId());
        userService.addChannel(channel.getOwnerId(), channel.getId());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, UUID requesterId, String name, String description) {
        Channel channel = findById(id);
        requireOwner(channel, requesterId, "채널 수정 권한이 없습니다.");
        channel.update(name, description);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Channel channel = findById(id);
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
