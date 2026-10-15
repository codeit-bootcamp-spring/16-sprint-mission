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
        // 채널 가입 여부 확인
        channel.getUsers().values()
                .stream()
                .filter(user -> user.getId() == sentFrom.getId())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("채널에 가입되지 않은 유저입니다."));

        Message message = new Message(sentFrom, content, channel);

        // 메세지 추가 시 유저와 채널에도 추가
        Map<UUID, Message> userMessages = sentFrom.getMessages();
        userMessages.put(message.getId(), message);
        sentFrom.updateMessages(userMessages);

        Map<UUID, Message> channelMessages = channel.getMessages();
        channelMessages.put(message.getId(), message);
        channel.updateMessages(channelMessages);

        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        Message message = this.findById(id);
        if (message == null) throw new NoSuchElementException("수정할 메세지가 존재하지 않습니다.");
        return message.updateContent(content);
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
