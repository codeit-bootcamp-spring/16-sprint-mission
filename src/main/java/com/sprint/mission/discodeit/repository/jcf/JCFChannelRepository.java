package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        Channel existingChannel = this.data.putIfAbsent(channel.getId(), channel);
        if (existingChannel != null) throw new IllegalStateException("이미 저장된 채널입니다.");
        return channel;
    }

    @Override
    public Channel update(UUID id, Channel newChannel) {
        return this.data.replace(id, newChannel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = this.data.get(id);
        if (channel != null && !channel.isDeleted()) return Optional.of(channel);
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().filter(channel -> !channel.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = this.data.get(id);
        channel.softDelete();
    }
}
