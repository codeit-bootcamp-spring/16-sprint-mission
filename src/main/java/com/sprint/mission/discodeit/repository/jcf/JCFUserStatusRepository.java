package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final List<UserStatus> userStatuses = new ArrayList<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        for (int i = 0; i < userStatuses.size(); i++) {
            if (userStatuses.get(i).getId().equals(userStatus.getId())) {
                userStatuses.set(i, userStatus);
                return userStatus;
            }
        }

            userStatuses.add(userStatus);
            return userStatus;
    }

    @Override
    public List<UserStatus> findAll() {
        return List.copyOf(userStatuses);
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return userStatuses.stream()
                .filter(userStatus ->
                        userStatus.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatuses.stream()
                .filter(userStatus ->
                        userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void delete(UserStatus userStatus) {
        userStatuses.removeIf(existingUserStatus ->
                existingUserStatus.getId().equals(userStatus.getId()));
    }
}
