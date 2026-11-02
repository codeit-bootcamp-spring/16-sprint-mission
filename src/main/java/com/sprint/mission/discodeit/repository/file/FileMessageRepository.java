package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final FileStore<Message> store = new FileStore<>("messages.ser");

    @Override
    public Message save(Message message) {
        Map<UUID, Message> data = store.load();
        data.put(message.getId(), message);
        store.write(data);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(store.load().get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(store.load().values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return store.load().values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findAllByUserId(UUID userId) {
        return store.load().values().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return store.load().containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> data = store.load();
        data.remove(id);
        store.write(data);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        Map<UUID, Message> data = store.load();
        data.values().removeIf(message -> message.getAuthorId().equals(userId));
        store.write(data);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        Map<UUID, Message> data = store.load();
        data.values().removeIf(message -> message.getChannelId().equals(channelId));
        store.write(data);
    }
}
