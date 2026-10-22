package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private final List<Message> messages = new ArrayList<>();

    @Override
    public Message save(Message message) {
        for (int i=0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                messages.set(i, message);
                return message;
            }
        }

        messages.add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(messages);
    }

    @Override
    public void delete(Message message) {
       messages.removeIf(existingMessage -> existingMessage.getId().equals(message.getId()));
    }
}