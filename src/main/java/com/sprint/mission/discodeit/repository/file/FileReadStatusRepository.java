package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.storage.FileObjectStore;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileReadStatusRepository
        implements ReadStatusRepository {

    private final FileObjectStore<ReadStatus> store;

    public FileReadStatusRepository(Path directory) {
        this.store = new FileObjectStore<>(
                directory.resolve("read-statuses"),
                ReadStatus.class
        );
    }

    @Override
    public ReadStatus save(ReadStatus entity) {
        return store.save(entity);
    }

    @Override
    public ReadStatus findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<ReadStatus> findAll() {
        return store.findAll();
    }

    @Override
    public void delete(UUID id) {
        store.delete(id);
    }
}