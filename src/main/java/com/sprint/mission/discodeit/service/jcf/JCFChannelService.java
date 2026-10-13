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
        Channel channel = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        return channel.update(name);
    }

    public Channel update(UUID id, Map<UUID, User> users) {
        Channel channel = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        return channel.update(users);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<List<Channel>> findAll() {
        return Optional.of(this.data.values().stream().toList());
    }

    @Override
    public Channel delete(UUID id) {
        this.findById(id).orElseThrow(() -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        return this.data.remove(id);
    }
}
