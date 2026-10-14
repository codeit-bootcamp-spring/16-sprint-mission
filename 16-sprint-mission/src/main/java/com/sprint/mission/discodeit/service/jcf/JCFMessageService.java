package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }
    @Override
    public Message create(String comment, UUID userId, UUID channelId) {
        Message message = new Message(comment, userId, channelId);
        data.add(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream().filter(message -> message.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return data.stream().collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, String comment) {
        Message message = findById(id);
        if (message == null) {
            return null;
        }
        message.update(comment);
        return message;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
    }
}
