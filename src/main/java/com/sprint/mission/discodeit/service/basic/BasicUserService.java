package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;
public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String username, String password, String phoneNumber, String email) {
        User user = new User(username, password, phoneNumber, email);
        return userRepository.save(user);
    }

    @Override
    public User read(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> readAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String username, String password, String phoneNumber, String email) {
        User user = userRepository.findById(id);
        if (user == null) {
        throw new NoSuchElementException("사용하지 않는 사용자입니다: " + id);
    }
    user.update(username,password,phoneNumber,email);
    return userRepository.save(user);
}

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
