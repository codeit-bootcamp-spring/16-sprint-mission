package com.sprint.mission.discodeit.service.service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String message, UUID channelId, UUID userId) {
        User user = userService.findById(userId).orElseThrow();
        Channel channel = channelService.findById(channelId).orElseThrow();

        Message newMessage = new Message(message, channelId, userId);
        data.put(newMessage.getId(), newMessage);
        user.addMessage(newMessage);
        channel.addMessage(newMessage);
        return newMessage;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            System.out.println("존재하지 않는 메시지입니다.");
            return Optional.empty();
        }
        return Optional.of(message);
    }

    @Override
    public List<Message> findAll() {
        Collection<Message> values = data.values();
        return new ArrayList<>(values);
    }

    @Override
    public Optional<Message> update(UUID id, String newMessage) {
        Optional<Message> message = findById(id);
        if (message.isEmpty()) {
            System.out.println("존재하지 않는 메시지의 내용을 수정할 수 없습니다.");
            return message;
        }
        message.get().setMessage(newMessage);
        message.get().setUpdatedAt(Instant.now().getEpochSecond());
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = data.remove(id);
        if (message == null) {
            throw new NoSuchElementException();
        }

        // Message를 작성한 User와 Message가 속한 Channel에서도 Message 정보 삭제
        userService.findById(message.getUserId()).ifPresent(user -> user.deleteMessage(message));
        channelService.findById(message.getChannelId()).ifPresent(channel -> channel.deleteMessage(message));
    }
}
