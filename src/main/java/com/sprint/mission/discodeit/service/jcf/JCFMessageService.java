package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new LinkedHashMap<>();
    }

    @Override
    public Message create(User sentFrom, String content, Channel channel) {
        Message message = new Message(sentFrom, content, channel);

        Map<UUID, Message> messages = sentFrom.getMessages();
        messages.put(message.getId(), message);
        sentFrom.updateMessages(messages);

        messages = channel.getMessages();
        messages.put(message.getId(), message);
        channel.updateMessages(messages);

        this.data.put(message.getId(), message);
        return message;
    }

    public Message updateSentFrom(UUID id, User user) {
        Message message = this.findById(id);
        if (message == null) throw new NoSuchElementException("수정할 메세지가 존재하지 않습니다.");
        return message.updateSentFrom(user);
    }

    public Message updateContent(UUID id, String content) {
        Message message = this.findById(id);
        if (message == null) throw new NoSuchElementException("수정할 메세지가 존재하지 않습니다.");
        return message.updateContent(content);
    }

    public Message updateChannel(UUID id, Channel channel) {
        Message message = this.findById(id);
        if (message == null) throw new NoSuchElementException("수정할 메세지가 존재하지 않습니다.");
        return message.updateChannel(channel);
    }

    @Override
    public Message findById(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Message message = this.findById(id);
        if (message == null) throw new NoSuchElementException("삭제할 메세지가 존재하지 않습니다.");

        // 메세지 삭제 시 채널과 유저에도 메세지 삭제
        message.getChannel().removeMessage(id);
        message.getSentFrom().removeMessage(id);

        this.data.remove(id);
    }
}
