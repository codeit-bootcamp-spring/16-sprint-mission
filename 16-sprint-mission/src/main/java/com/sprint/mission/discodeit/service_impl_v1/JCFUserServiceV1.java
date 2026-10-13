package com.sprint.mission.discodeit.service_impl_v1;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.*;

public class JCFUserServiceV1 implements UserService {
    private final Map<String, User> data;

    public JCFUserServiceV1() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String userName, String email, String password) {
        User newUser = new User(userName, email, password);
        data.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User findById(String id) {
        User user = data.get(id);
        if (user == null) {
            System.out.println("존재하지 않는 유저입니다");
        }
        return user;
    }

    public List<User> findAll() {
        Collection<User> values = data.values();
        return new ArrayList<>(values);
    }

    @Override
    public void updatePw(String id, String newPw) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldPw = scanner.nextLine();
            if (oldPw.equals(user.getPassword())) {
                user.setPassword(newPw);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
        } else {
            System.out.println("존재하지 않는 유저입니다.");
        }
    }

    @Override
    public void updateUsername(String id, String newUsername) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldUsername = scanner.nextLine();
            if (oldUsername.equals(user.getUserName())) {
                user.setUserName(newUsername);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
        } else {
            System.out.println("존재하지 않는 유저입니다.");
        }
    }

    @Override
    public void updateEmail(String id, String newEmail) {
        if (data.containsKey(id)) {
            User user = data.get(id);

            Scanner scanner = new Scanner(System.in);
            String oldEmail = scanner.nextLine();
            if (oldEmail.equals(user.getEmail())) {
                user.setEmail(newEmail);
                user.setUpdatedAt(Instant.now().getEpochSecond());
            }
        } else {
            System.out.println("존재하지 않는 유저입니다.");
        }
    }

    @Override
    public User delete(String id) {
        User removedUser = data.remove(id);
        if (removedUser == null) {
            System.out.println("존재하지 않는 유저입니다");
        }
        return removedUser;
    }
}
