package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> users = new ArrayList<>();

    @Override
    public User createUser(String name, int age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상이어야 합니다.");
        }

        User user = new User(name, age);
        users.add(user);

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findByName(String name) {
        return users.stream()
                .filter(user -> user.getName().equals(name))
                .toList();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users);
    }

    @Override
    public User updateUser(UUID id, String name, int age) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상이어야 합니다.");
        }

        user.update(name, age);
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );
        users.remove(user);
    }

    @Override
    public void addChannelToUser(UUID id, Channel channel) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );
        user.addChannel(channel);
    }

    @Override
    public void removeChannelFromUser(UUID id, Channel channel) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다")
                );
        user.removeChannel(channel);
    }

    @Override
    public void addMessageToUser(UUID id, Message message){
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다")
                );
        user.addMessage(message);
    }

    @Override
    public void removeMessageFromUser(UUID id, Message message){
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다")
                );
        user.removeMessage(message);
    }
}
