package com.sprint.mission.discodeit.service_impl;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.null_entity.NullMessage;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message create(String message, UUID channelId, UUID userId) {
        Message newMessage = new Message(message, channelId, userId);
        data.put(newMessage.getId(), newMessage);
        return newMessage;
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            System.out.println("존재하지 않는 메시지입니다.");
            return new NullMessage();
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }


    @Override
    public void update(UUID id, String newMessage) {
        Message message = findById(id);
        if (message instanceof NullMessage) {
            System.out.println("존재하지 않는 메시지의 내용을 수정할 수 없습니다.");
            return;
        }
        message.setMessage(newMessage);
        message.setUpdatedAt(Instant.now().getEpochSecond());
    }

    @Override
    public Message delete(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            System.out.println("존재하지 않는 id입니다.");
            return new NullMessage();
        }
        return message;
    }
}
