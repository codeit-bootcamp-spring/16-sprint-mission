package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    public void create(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null.");
        }

        if (data.containsKey(channel.getId())) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + channel.getId());
        }

        data.put(channel.getId(), channel);
    }

    public Channel findById(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException(
                    "Not Found channel id: " + id
            );
        }

        return channel;
    }

    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    public void update(UUID id, String name, String description) {
        Channel existingChannel = data.get(id);

        if (existingChannel == null) {
            throw new IllegalArgumentException(
                    "채널을 찾을 수 없습니다: " + id
            );
        }

        existingChannel.update(name, description);
    }

    public void delete(UUID id) {
        Channel removedChannel = data.remove(id);
        if (removedChannel == null) {
            throw new IllegalArgumentException(
                    "Not Found channel id: " + id
            );
        }
    }
}
