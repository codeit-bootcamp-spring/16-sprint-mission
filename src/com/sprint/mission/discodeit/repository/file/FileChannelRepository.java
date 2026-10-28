package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private final File file;

    public FileChannelRepository() {
        this.file = new File("data/channels.dat");

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널 정보는 필수입니다.");
        }

        Map<UUID, Channel> data = readAllAsMap();
        data.put(channel.getId(), channel);
        writeAll(data);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(readAllAsMap().get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(readAllAsMap().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> data = readAllAsMap();

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        data.remove(id);
        writeAll(data);
    }

    private Map<UUID, Channel> readAllAsMap() {
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("채널 파일을 읽을 수 없습니다.", e);
        }
    }

    private void writeAll(Map<UUID, Channel> data) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("채널 파일을 저장할 수 없습니다.", e);
        }
    }
}
