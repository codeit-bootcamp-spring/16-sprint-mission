package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        for (int i=0; i < users.size(); i++) {
            // 만약 존재한다면 수정하기
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                return user;
            }
        }

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
    public List<User> findAllByChannelId(UUID channelId) {
        return users.stream()
                .filter(user ->
                        user.getChannels().stream()
                                .anyMatch(channel ->
                                        channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public void delete(User user) {
        users.removeIf(existingUser -> existingUser.getId().equals(user.getId()));
    }
}
