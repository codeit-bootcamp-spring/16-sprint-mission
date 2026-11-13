package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
}

    public void create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if (data.containsKey(user.getId())) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + user.getId());
        }

        validateEmail(user.getEmail());

        if (isEmailDuplicate(user.getEmail(), null)) {
            throw new IllegalArgumentException(
                    "Email is already in use." + user.getEmail()
            );
        }

        data.put(user.getId(), user);
    }

    public User findById(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "Not Found user id: " + id
            );
        }

        return user;
    }

    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    public void update(UUID id, User user) {
        User existingUser = data.get(id);

        if (existingUser == null) {
            throw new IllegalArgumentException(
                    "Not Found User: " + id
            );
        }

        validateEmail(user.getEmail());

        if (isEmailDuplicate(user.getEmail(), id)) {
            throw new IllegalArgumentException(
                    "Email is already in use." + user.getEmail()
            );
        }

        existingUser.update(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                existingUser.getProfileId()
        );
    }

    public void delete(UUID id) {
        User removedUser = data.remove(id);
        if (removedUser == null) {
            throw new IllegalArgumentException(
                    "Not Found User: " + id
            );
        }
    }

    private boolean isEmailDuplicate(String email, UUID excludUserId) {
        return data.values().stream()
                .anyMatch(user ->
                        user.getEmail().equalsIgnoreCase(email)
                                && !user.getId().equals(excludUserId)
                );
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다." + email);
        }
    }
}
