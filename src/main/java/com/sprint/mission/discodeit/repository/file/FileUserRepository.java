package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.FileObjectStore;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final FileObjectStore<User> store;

    public FileUserRepository(Path directory) {
        this.store = new FileObjectStore<>(directory.resolve("users"), User.class);
    }

    @Override
    public User save(User entity) {
        return store.save(entity);
    }

    @Override
    public User findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<User> findAll() {
        return store.findAll();
    }

    @Override
    public void delete(UUID id) {
        store.delete(id);
    }
}