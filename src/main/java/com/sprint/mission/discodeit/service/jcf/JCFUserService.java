package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = new User(username);
        this.data.add(user);
        return user;
    }

    /* User의 UUID를 가져오기 위한 메서드입니다. */
    public UUID getUserUUID(int index) {
        List<User> list = this.data;
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return list.get(index).getId();
    }

    @Override
    public List<User> findAll() {
        List<User> userList = this.data.stream()
                .toList();

        return userList;
    }

    @Override
    public User findById(UUID id) {
        User user = this.data.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (user == null) {
            throw new NoSuchElementException("User not found");
        }

        return user;
    }

    @Override
    public void updateUsername(UUID id, String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = this.findById(id);
        user.updateUsername(username);
    }

    @Override
    public boolean deleteUser(UUID id) {
        User user = this.findById(id);
        return this.data.remove(user);
    }
}
