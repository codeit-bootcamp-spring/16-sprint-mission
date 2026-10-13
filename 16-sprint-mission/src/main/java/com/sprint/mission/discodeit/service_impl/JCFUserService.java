package com.sprint.mission.discodeit.service_impl;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.null_entity.NullUser;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String userName, String email, String password) {
        User newUser = new User(userName, email, password);
        data.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);
        if (user == null) {
            System.out.println("존재하지 않는 유저입니다");
            return new NullUser();
        }
        return user;
    }

    @Override
    public User findByName(String userName) {
        Collection<User> values = data.values();
        for (User user : values) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return new NullUser();
    }

    public List<User> findAll() {
        Collection<User> values = data.values();
        return new ArrayList<>(values);
    }

    @Override
    public User updatePw(UUID id, String newPw) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldPw = scanner.nextLine();
            if (oldPw.equals(user.getPassword())) {
                user.setPassword(newPw);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            return user;
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return new NullUser();
        }
    }

    @Override
    public User updateUsername(UUID id, String newUsername) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldUsername = scanner.nextLine();
            if (oldUsername.equals(user.getUserName())) {
                user.setUserName(newUsername);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            return user;
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return new NullUser();
        }
    }

    @Override
    public User updateEmail(UUID id, String newEmail) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldEmail = scanner.nextLine();
            if (oldEmail.equals(user.getEmail())) {
                user.setEmail(newEmail);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
            return user;
        } else {
            System.out.println("존재하지 않는 유저입니다.");
            return new NullUser();
        }
    }

    @Override
    public User delete(UUID id) {
        User removedUser = data.remove(id);
        if (removedUser == null) {
            System.out.println("존재하지 않는 유저입니다");
            return new NullUser();
        }
        return removedUser;
    }
}
