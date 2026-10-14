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
    public void add(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel getById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, String name) {
        Channel channel = data.get(id);
        channel.update(name);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}