package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    public FileChannelRepository() {
        Path path = Paths.get("./data/channel.ser");
        if (Files.exists(path) && Files.isRegularFile(path)) {
            System.out.println("저장된 데이터를 불러옵니다.");
            return;
        }
        this.writeChannelFile(new HashMap<>());
    }

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> channels = this.readChannelFile();
        Channel existingChannel = channels.putIfAbsent(channel.getId(), channel);
        if (existingChannel != null) throw new IllegalStateException("이미 저장된 채널입니다.");
        this.writeChannelFile(channels);
        return channel;
    }

    @Override
    public Channel update(UUID id, Channel newChannel) {
        Map<UUID, Channel> channels = this.readChannelFile();
        Channel channel = channels.replace(id, newChannel);
        this.writeChannelFile(channels);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Map<UUID, Channel> channels = this.readChannelFile();
        Channel channel = channels.get(id);
        if (channel != null && !channel.isDeleted()) return Optional.of(channel);
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> channels = this.readChannelFile();
        return channels.values().stream().filter(channel -> !channel.isDeleted()).toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> channels = this.readChannelFile();
        Channel channel = channels.get(id);
        channel.softDelete();
        this.writeChannelFile(channels);
    }

    public Map<UUID, Channel> readChannelFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./data/channel.ser"))) {
            @SuppressWarnings("unchecked")
            Map<UUID, Channel> channels = (Map<UUID, Channel>) ois.readObject();
            return channels;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError("직렬화된 객체의 클래스를 찾을 수 없습니다.");
        }
    }

    public void writeChannelFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./data/channel.ser"))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        }
    }
}
