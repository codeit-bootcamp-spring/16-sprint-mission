package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.MessageService;

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
    public Message create(Message message) {
        if (userService.findById(message.getUser().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 User는 Message를 작성할 수 없습니다.");
        }
        if (channelService.findById(message.getChannel().getId()).isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 Channel에는 Message를 작성할 수 없습니다.");
        }
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String newMessage) {
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
        message.update(newMessage);
        return message;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다.");
        }
    }
}
