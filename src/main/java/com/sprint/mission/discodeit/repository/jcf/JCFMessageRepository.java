package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messages = new LinkedHashMap<>();

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    @Override
    public List<Message> findAll() {
        List<Message> allMessages = new ArrayList<>(messages.values());
        allMessages.sort(Comparator.comparing(Message::getCreatedAt));
        return allMessages;
    }

    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public void deleteById(UUID messageId) {
        messages.remove(messageId);
    }

    @Override
    public List<Message> findByUserId(UUID userId) {
        return messages.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getSenderId().equals(userId)
                        && message.getChannelId().equals(channelId))
                .toList();
    }
}
