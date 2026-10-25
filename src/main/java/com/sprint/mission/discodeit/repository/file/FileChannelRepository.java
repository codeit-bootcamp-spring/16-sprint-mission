package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository extends FileRepository<Channel> implements ChannelRepository {
    private final String filePath = "./data/channel.ser";

    public FileChannelRepository() {
        if (this.checkIfFileExists(filePath)) {
            System.out.printf("파일을 찾았습니다: %s, 저장된 데이터를 불러옵니다.\n".formatted(filePath));
            return;
        }
        System.out.printf("기존 파일이 없습니다. 새 데이터를 저장합니다: %s\n".formatted(filePath));
        this.writeFile(new HashMap<>(), filePath);
    }

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> channels = this.readFile(filePath);
        Channel existingChannel = channels.putIfAbsent(channel.getId(), channel);
        if (existingChannel != null) throw new IllegalStateException("이미 저장된 채널입니다.");
        this.writeFile(channels, filePath);
        return channel;
    }

    @Override
    public Channel update(UUID id, Channel newChannel) {
        Map<UUID, Channel> channels = this.readFile(filePath);
        Channel channel = channels.replace(id, newChannel);
        this.writeFile(channels, filePath);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Map<UUID, Channel> channels = this.readFile(filePath);
        Channel channel = channels.get(id);
        if (channel != null && !channel.isDeleted()) return Optional.of(channel);
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> channels = this.readFile(filePath);
        return channels.values().stream().filter(channel -> !channel.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> channels = this.readFile(filePath);
        Channel channel = channels.get(id);
        channel.softDelete();
        this.writeFile(channels, filePath);
    }
}
