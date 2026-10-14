package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, UUID requesterId, String channelName, String description) {
        Channel channel = findById(id);

        if (!channel.getOwnerId().equals(requesterId)) {
            throw new IllegalArgumentException("채널 수정 권한이 없습니다.");
        }

        channel.updateChannelName(channelName);
        channel.updateDescription(description);
        data.put(id, channel);
        return channel;
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Channel channel = findById(id);

        if(!channel.getOwnerId().equals(requesterId)) {
            throw new IllegalArgumentException("채널 삭제 권한이 없습니다.");
        }

        data.remove(id);
    }

    @Override
    public void joinChannel(UUID id, UUID userId) {
        findById(id);
        userService.findById(userId);
        userService.addChannel(userId, id);
    }

    @Override
    public void leaveChannel(UUID id, UUID userId) {
        Channel channel = findById(id);
        User user = userService.findById(userId);

        if (!user.getChannels().contains(userId)) {
            throw new IllegalArgumentException("채널 멤버가 아닙니다.");
        }

        if (channel.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("채널 소유주는 퇴장이 불가합니다.");
        }

        userService.removeChannel(userId, id);
    }

    @Override
    public void transformOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId) {
        Channel channel = findById(id);
        User newOwner = userService.findById(newOwnerId);

        if (!channel.getOwnerId().equals(currentOwnerId)) {
            throw new IllegalArgumentException("소유자만 소유권을 양도 가능합니다.");
        }

        if (!newOwner.getChannels().contains(id)) {
            throw new IllegalArgumentException("채널 멤버에게만 양도 가능합니다.");
        }
        channel.transferOwnership(newOwnerId);
    }

    @Override
    public boolean addMessage(UUID id, UUID messageId) {
        Channel channel = findById(id);
        return channel.addMessage(messageId);
    }
}
