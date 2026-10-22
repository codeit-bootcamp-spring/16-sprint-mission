package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final File file = new File("data/channels.ser");

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 읽기 실패", e);
        }
    }

    private void saveAll(Map<UUID, Channel> data) {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패", e);
        }
    }


    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = load();
        data.put(channel.getId(), channel);
        saveAll(data);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return load().get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> data = load();
        data.remove(id);
        saveAll(data);
    }
}