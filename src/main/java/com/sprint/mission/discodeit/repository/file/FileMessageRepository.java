package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.storage.FileObjectStore;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final FileObjectStore<Message> store;

    public FileMessageRepository(Path directory) {
        this.store = new FileObjectStore<>(directory.resolve("messages"), Message.class);
    }

    @Override
    public Message save(Message entity) {
        return store.save(entity);
    }

    @Override
    public Message findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return store.findAll();
    }

    @Override
    public void delete(UUID id) {
        store.delete(id);
    }
}