package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelService {
    private static final Path PATH = Paths.get("data", "channels.ser");
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Channel create(Channel channel) {
        Map<UUID, Channel> data = load();
        userService.findById(channel.getOwnerId());
        data.put(channel.getId(), channel);
        userService.addChannel(channel.getOwnerId(), channel.getId());
        save(data);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return getOrThrow(load(), id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public Channel update(UUID id, UUID requesterId, String name, String description) {
        Map<UUID, Channel> data = load();
        Channel channel = getOrThrow(data, id);
        requireOwner(channel, requesterId, "채널 수정 권한이 없습니다.");
        channel.update(name, description);
        save(data);
        return channel;
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Map<UUID, Channel> data = load();
        Channel channel = getOrThrow(data, id);
        requireOwner(channel, requesterId, "채널 삭제 권한이 없습니다.");
        if (!channel.getMessages().isEmpty()) {
            throw new IllegalArgumentException("메시지가 남아 있어 삭제할 수 없습니다.");
        }

        for (UUID userId : channel.getUsers()) {           // 아래(User) 호출
            userService.removeChannel(userId, id);
        }
        data.remove(id);
        save(data);
    }

    @Override
    public void join(UUID id, UUID userId) {
        Map<UUID, Channel> data = load();
        Channel channel = getOrThrow(data, id);
        userService.findById(userId);
        channel.addUser(userId);
        userService.addChannel(userId, id);
        save(data);
    }

    @Override
    public void leave(UUID id, UUID userId) {
        Map<UUID, Channel> data = load();
        Channel channel = getOrThrow(data, id);
        userService.findById(userId);
        channel.removeUser(userId);
        userService.removeChannel(userId, id);
        save(data);
    }

    @Override
    public void transferOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId) {
        Map<UUID, Channel> data = load();
        Channel channel = getOrThrow(data, id);
        requireOwner(channel, currentOwnerId, "소유자만 양도 가능합니다.");
        channel.transferOwnership(newOwnerId);
        save(data);
    }

    @Override
    public void addMessage(UUID id, UUID messageId) {
        Map<UUID, Channel> data = load();
        getOrThrow(data, id).addMessage(messageId);
        save(data);
    }

    @Override
    public void removeMessage(UUID id, UUID messageId) {
        Map<UUID, Channel> data = load();
        getOrThrow(data, id).removeMessage(messageId);
        save(data);
    }

    private static Channel getOrThrow(Map<UUID, Channel> data, UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    private void requireOwner(Channel channel, UUID requesterId, String message) {
        if (!channel.isOwner(requesterId)) {
            throw new IllegalArgumentException(message);
        }
    }

    private void save(Map<UUID, Channel> data) {
        try {
            Files.createDirectories(PATH.getParent());           // data/ 폴더 보장
            try (ObjectOutputStream oos =
                         new ObjectOutputStream(Files.newOutputStream(PATH))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
