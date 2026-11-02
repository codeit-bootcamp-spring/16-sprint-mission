package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository extends FileRepository<Channel> implements ChannelRepository {
    public FileChannelRepository() {
        super("channel.ser", new HashMap<>());
    }

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> channels = this.readFile(resolvedPath);
        Channel existingChannel = channels.putIfAbsent(channel.getId(), channel);
        if (existingChannel != null) throw new IllegalStateException("이미 저장된 채널입니다.");
        this.writeFile(channels, resolvedPath);
        return channel;
    }

    @Override
    public Channel update(UUID id, Channel newChannel) {
        Map<UUID, Channel> channels = this.readFile(resolvedPath);
        Channel existing = channels.replace(id, newChannel);
        if (existing == null) throw new IllegalStateException("저장되지 않은 채널입니다.");
        this.writeFile(channels, resolvedPath);
        return newChannel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Map<UUID, Channel> channels = this.readFile(resolvedPath);
        Channel channel = channels.get(id);
        if (channel != null && !channel.isDeleted()) return Optional.of(channel);
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> channels = this.readFile(resolvedPath);
        return channels.values().stream().filter(channel -> !channel.isDeleted()).toList();
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        Map<UUID, Channel> channels = this.readFile(resolvedPath);
        return channels.values()
                .stream()
                .filter(channel -> channel.getUsers().contains(userId))
                .filter(channel -> !channel.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> channels = this.readFile(resolvedPath);
        Channel channel = channels.get(id);
        if (channel == null || channel.isDeleted()) throw new IllegalArgumentException("삭제할 채널이 존재하지 않습니다.");
        channel.softDelete();
        this.writeFile(channels, resolvedPath);
    }
}
