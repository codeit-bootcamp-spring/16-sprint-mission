package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User add(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    public User getById(UUID id) {
        return userRepository.getById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User update(UUID id, String username) {
        User user = userRepository.getById(id);
        user.update(username);
        userRepository.save(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}