package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository extends FileRepository<UserStatus> implements UserStatusRepository {
    public FileUserStatusRepository() {
        super("./data/user_status.ser", new HashMap<>());
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Map<UUID, UserStatus> userStatusMap = this.readFile(filePath);
        UserStatus existing = userStatusMap.putIfAbsent(userStatus.getId(), userStatus);
        if (existing != null) throw new IllegalStateException("이미 저장된 유저 접속 기록입니다.");
        this.writeFile(userStatusMap, filePath);
        return userStatus;
    }

    @Override
    public UserStatus update(UUID id, UserStatus userStatus) {
        Map<UUID, UserStatus> userStatusMap = this.readFile(filePath);
        UserStatus existing = userStatusMap.replace(id, userStatus);
        if (existing == null) throw new IllegalStateException("저장되지 않은 유저 접속 기록입니다.");
        this.writeFile(userStatusMap, filePath);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Map<UUID, UserStatus> userStatusMap = this.readFile(filePath);
        UserStatus userStatus = userStatusMap.get(id);
        if (userStatus != null && !userStatus.isDeleted()) return Optional.of(userStatus);
        return Optional.empty();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        Map<UUID, UserStatus> userStatusMap = this.readFile(filePath);
        return userStatusMap.values()
                .stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .filter(userStatus -> !userStatus.isDeleted())
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        Map<UUID, UserStatus> userStatusMap = this.readFile(filePath);
        return userStatusMap.values()
                .stream()
                .filter(userStatus -> !userStatus.isDeleted())
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, UserStatus> userStatusMap = this.readFile(filePath);
        UserStatus userStatus = userStatusMap.get(id);
        if (userStatus == null || userStatus.isDeleted())
            throw new IllegalArgumentException("삭제할 유저 접속 기록이 존재하지 않습니다.");
        userStatus.softDelete();
        this.writeFile(userStatusMap, filePath);
    }
}
