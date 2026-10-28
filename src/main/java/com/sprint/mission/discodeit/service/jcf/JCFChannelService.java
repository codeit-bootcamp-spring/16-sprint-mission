package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new LinkedHashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String name, String description) {
        return create(ChannelType.PUBLIC, name, description);
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        if (type == null) {
            throw new IllegalArgumentException("채널 타입이 없습니다.");
        }

        validateChannel(name, description, null);
        Channel channel = new Channel(type, name.trim(), description);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = findById(id);
        validateChannel(name, description, id);
        channel.update(name.trim(), description);
        data.put(id, channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);

        if (!channel.getMessageIds().isEmpty()) {
            throw new IllegalStateException("메시지가 남아 있어 채널을 삭제할 수 없습니다.");
        }

        if (!channel.getMemberIds().isEmpty()) {
            throw new IllegalStateException("참여 중인 사용자가 있어 채널을 삭제할 수 없습니다.");
        }

        data.remove(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        if (channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("이미 참여 중인 사용자입니다.");
        }

        channel.addMember(userId);
        user.addChannel(channelId);
        data.put(channel.getId(), channel);
        userService.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("참여하지 않은 사용자입니다.");
        }

        for (UUID messageId : user.getMessageIds()) {
            if (channel.getMessageIds().contains(messageId)) {
                throw new IllegalStateException("채널에 작성한 메시지가 남아 있어 탈퇴할 수 없습니다.");
            }
        }

        channel.removeMember(userId);
        user.removeChannel(channelId);
        data.put(channel.getId(), channel);
        userService.save(user);
    }

    private void validateChannel(String name, String description, UUID excludeId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름을 입력해야 합니다.");
        }

        if (description == null) {
            throw new IllegalArgumentException("채널 설명은 null일 수 없습니다.");
        }

        for (Channel channel : data.values()) {
            if (!channel.getId().equals(excludeId)
                    && channel.getName().equalsIgnoreCase(name.trim())) {
                throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
            }
        }
    }
}