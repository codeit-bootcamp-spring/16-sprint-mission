package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import java.util.NoSuchElementException;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String channelName, String topic, String type) {
        Channel channel = new Channel(channelName, topic, type);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String channelName, String topic, String type) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다." + id);
        }
        channel.update(channelName, topic, type);
        return channel;
    }

    @Override
    public void addParticipant(UUID channelId, UUID userId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다." + channelId);
        }
        if (userService.read(userId) == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다." + userId);
        }
        channel.addParticipant(userId);
    }

    @Override
    public void removeParticipant(UUID channelId, UUID userId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다." + channelId);
        }
        if (userService.read(userId) == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다." + userId);
        }
        channel.removeParticipant(userId);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}