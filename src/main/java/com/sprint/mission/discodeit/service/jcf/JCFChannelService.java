package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        this.data.put(channel.getId(), channel);
        return channel;
    }

    public Channel update(UUID id, String name) {
        Channel channel = this.findById(id);
        if (channel == null) throw new NoSuchElementException("수정할 채널이 존재하지 않습니다.");
        return channel.update(name);
    }

    public Channel update(UUID id, Map<UUID, User> users) {
        Channel channel = this.findById(id);
        if (channel == null) throw new NoSuchElementException("수정할 채널이 존재하지 않습니다.");
        return channel.update(users);
    }

    @Override
    public Channel findById(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = this.findById(id);
        if (channel == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");
        this.data.remove(id);
    }
}
