package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.FileObjectStore;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileUserStatusRepository
        implements UserStatusRepository {

    private final FileObjectStore<UserStatus> store;

    public FileUserStatusRepository(Path directory) {
        this.store = new FileObjectStore<>(
                directory.resolve("user-statuses"),
                UserStatus.class
        );
    }

    @Override
    public UserStatus save(UserStatus entity) {
        return store.save(entity);
    }

    @Override
    public UserStatus findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return store.findAll();
    }

    @Override
    public void delete(UUID id) {
        store.delete(id);
    }
}