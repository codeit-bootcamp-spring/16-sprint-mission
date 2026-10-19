package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserService implements UserService {
    private static final Path PATH = Paths.get("data", "users.ser");

    @Override
    public User create(User user) {
        Map<UUID, User> data = load();
        validateEmailUnique(data, user.getEmail(), null);
        data.put(user.getId(), user);
        save(data);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return getOrThrow(load(), id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public List<User> findAllByChannelId(UUID channelId) {
        return load().values().stream()
                .filter(user -> user.hasChannel(channelId))
                .toList();
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        Map<UUID, User> data = load();
        User user = findById(id);
        user.update(username, email, password);
        save(data);
        return user;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = load();
        User user = getOrThrow(data, id);

        if (!user.getChannels().isEmpty()) {
            throw new IllegalArgumentException("참여 중인 채널이 있어 삭제할 수 없습니다.");
        }

        if (!user.getMessages().isEmpty()) {
            throw new IllegalArgumentException("작성한 메시지가 있어 삭제할 수 없습니다.");
        }

        data.remove(id);
        save(data);
    }

    @Override
    public void addChannel(UUID id, UUID channelId) {
        Map<UUID, User> data = load();
        getOrThrow(data, id).addChannel(channelId);
        save(data);
    }

    @Override
    public void removeChannel(UUID id, UUID channelId) {
        Map<UUID, User> data = load();
        getOrThrow(data, id).removeChannel(channelId);
        save(data);
    }

    @Override
    public void addMessage(UUID id, UUID messageId) {
        Map<UUID, User> data = load();
        getOrThrow(data, id).addMessage(messageId);
        save(data);
    }

    @Override
    public void removeMessage(UUID id, UUID messageId) {
        Map<UUID, User> data = load();
        getOrThrow(data, id).removeMessage(messageId);
        save(data);
    }

    private User getOrThrow(Map<UUID, User> data, UUID id) {
        User user = data.get(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    private void validateEmailUnique(Map<UUID, User> data, String email, UUID excludeUserId) {
        for (User value : data.values()) {
            if (value.getEmail().equals(email) && !value.getId().equals(excludeUserId)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }

    private void save(Map<UUID, User> data) {
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
    private Map<UUID, User> load() {
        if (!Files.exists(PATH)) return new HashMap<>();
        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(PATH))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
