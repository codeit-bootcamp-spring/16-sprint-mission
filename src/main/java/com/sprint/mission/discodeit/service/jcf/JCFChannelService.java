package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel add(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getById(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = getById(id);
        channel.update(name);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        data.remove(id);
    }
}