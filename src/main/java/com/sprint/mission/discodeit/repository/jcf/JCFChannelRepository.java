package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
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
        Channel existing = this.data.replace(id, newChannel);
        if (existing == null) throw new IllegalStateException("저장되지 않은 채널입니다.");
        return newChannel;
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
    public List<Channel> findAllByUserId(UUID userId) {
        return this.data.values()
                .stream()
                .filter(channel -> channel.getUsers().contains(userId))
                .filter(channel -> !channel.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = this.data.get(id);
        if (channel == null || channel.isDeleted()) throw new IllegalArgumentException("삭제할 채널이 존재하지 않습니다.");
        channel.softDelete();
    }
}
