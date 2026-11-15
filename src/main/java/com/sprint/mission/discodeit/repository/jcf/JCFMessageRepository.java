package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messages = new ConcurrentHashMap<>();

    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(messages.values());
    }

    @Override
    public List<Message> findAllByUserId(UUID userId) {
        return messages.values().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void delete(Message message) {
        messages.remove(message.getId());
    }
}