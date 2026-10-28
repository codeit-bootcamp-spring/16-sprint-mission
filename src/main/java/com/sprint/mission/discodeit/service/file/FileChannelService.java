package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService{
    private final File file = new File("data/channels.ser");
    private final UserService userService;
public FileChannelService(UserService userService) {
    this.userService = userService;
}

    @SuppressWarnings("unchecked")
    private Map<UUID,Channel> load() {
        if(!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 읽기 실패", e);
        }
    }

    private void save(Map<UUID, Channel> data) {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패", e);
        }
    }

    @Override
    public Channel create(String channelname, String topic, String type) {
        Map<UUID, Channel>data = load();
        Channel channel = new Channel(channelname,topic,type);
        data.put(channel.getId(), channel);
        save(data);
        return channel;
    }
    @Override
    public Channel read(UUID id) {
        return load().get(id);
    }
    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(load().values());
    }
    @Override
    public Channel update(UUID id, String channelName, String topic, String type) {
        Map<UUID, Channel> data = load();
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channer입니다." + id);
        }
        channel.update(channelName, topic,type);
        save(data);
        return channel;
    }

    @Override
    public void addParticipant(UUID channelId, UUID userId) {
        Map<UUID, Channel> data = load();
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channer입니다." + channelId);
        }
        if (userService.read(userId) == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다" + userId);
        }
        channel.addParticipant(userId);
        save(data);
    }
    @Override
    public void removeParticipant(UUID channelId, UUID userId) {
        Map<UUID, Channel> data = load();
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channer입니다." + channelId);
        }
        if (userService.read(userId) == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다" + userId);
        }
        channel.removeParticipant(userId);
        save(data);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = load();
        data.remove(id);
        save(data);
    }


}
