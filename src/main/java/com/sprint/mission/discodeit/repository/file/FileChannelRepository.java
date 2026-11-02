package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileChannelRepository implements ChannelRepository  {
    private final FileStore<Channel> store = new FileStore<>("channels.ser");

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = store.load();
        data.put(channel.getId(), channel);
        store.write(data);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(store.load().get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(store.load().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return store.load().values().stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> data = store.load();
        data.remove(id);
        store.write(data);
    }
}
