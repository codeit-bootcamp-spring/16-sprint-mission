package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message createMessage(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        Message message = new Message(content);
        this.data.add(message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messageList = this.data.stream()
                .toList();

        return messageList;
    }

    @Override
    public Message findById(UUID uuid) {
        Message message = this.data.stream()
                .filter(m -> m.getId().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException());

        return message;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }

        Message message = this.findById(id);
        message.updateContent(content);

        return message;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message message = this.findById(uuid);
        this.data.remove(message);
    }
}
