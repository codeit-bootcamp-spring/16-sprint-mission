package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private final Path filePath = Paths.get("data", "channels.dat");
    private final Map<UUID, Channel> data;

    public FileChannelRepository() {
        this.data = loadData();
    }

    private Map<UUID, Channel> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, Channel>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("채널 데이터를 불러오지 못했습니다.", e);
        }
    }

    private void saveData() {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream output =
                         new ObjectOutputStream(Files.newOutputStream(filePath))) {
                output.writeObject(data);
            }
        } catch (IOException e) {
            throw new IllegalStateException("채널 데이터를 저장하지 못했습니다.", e);
        }
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
        saveData();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveData();
    }
}
