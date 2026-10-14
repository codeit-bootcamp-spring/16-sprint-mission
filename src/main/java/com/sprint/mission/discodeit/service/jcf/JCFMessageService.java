package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class JCFMessageService implements MessageService {

    final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new ConcurrentHashMap<>();
        //this.messageService = messageService;
    }

    @Override
    public Message save(Message message) {
        if (message == null || message.getId() == null) {
            throw new NullPointerException("데이터가 이상함");
        }
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public List<Message> findId(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            return Collections.emptyList();
        }
        List<Message> result = new ArrayList<>();
        result.add(message);
        return result;
    }

    @Override
    public List<Message> findUserlId(UUID userId) {
        return data.values().stream()
                .filter(message -> message.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message->message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = data.get(id);
        if (message == null) {
            throw new NullPointerException("해당메시지 없음");
        }
        message.setContent(content);
        return message;
    }

    @Override
    public void deleteId(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(message -> message.getChannelId().equals(channelId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.values().removeIf(message -> message.getUserId().equals(userId));
    }

}
