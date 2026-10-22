package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new LinkedHashMap<>();
    }

    @Override
    public Message create(UUID sentFromId, String content, UUID channelId) {
        Message message = new Message(sentFromId, content, channelId);

        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message update(UUID id, Message newMessage) {
        Message message = this.findById(id).orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다."));
        message.validateNotDeleted();
        message.update(newMessage);
        this.data.put(id, message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Message message = this.findById(id).orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 않습니다."));
        message.softDelete();
    }
}
