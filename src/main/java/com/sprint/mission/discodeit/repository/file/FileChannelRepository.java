package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.storage.FileObjectStore;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final FileObjectStore<Channel> store;

    public FileChannelRepository(Path directory) {
        this.store = new FileObjectStore<>(directory.resolve("channels"), Channel.class);
    }

    @Override
    public Channel save(Channel entity) {
        return store.save(entity);
    }

    @Override
    public Channel findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return store.findAll();
    }

    @Override
    public void delete(UUID id) {
        store.delete(id);
    }
}