package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> messages;

    public JCFMessageRepository() {
        this.messages = new ArrayList<Message>();
    }

    @Override
    public Message save(Message message) {
        messages.add(message);
        return message;
    }

    @Override
    public List<Message> getMessages() {
        return List.copyOf(this.messages);
    }

    @Override
    public Message getMessage(UUID id) {
        Message message = this.messages.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 ID의 메시지를 찾을 수 없습니다: " + id)
                );

        return message;
    }

    @Override
    public void removeMessage(UUID id) {
        Message message = this.getMessage(id);
        this.messages.remove(message);
    }

}
