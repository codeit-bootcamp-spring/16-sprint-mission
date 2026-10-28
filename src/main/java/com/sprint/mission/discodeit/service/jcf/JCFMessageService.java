package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(
            UserService userService,
            ChannelService channelService
    ) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    // Create
    @Override
    public void create(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }

        if (data.containsKey(message.getId())) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + message.getId());
        }

        // User, Channel 조회
        User user = userService.findById(message.getUserId());
        Channel channel = channelService.findById(message.getChannelId());

        data.put(message.getId(), message);
    }

    // Read
    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException(
                    "Not Found Message id: " + id
            );
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    // Update
    @Override
    public void update(UUID id, String content) {
        Message existingMessage = data.get(id);

        if (existingMessage == null) {
            throw new IllegalArgumentException(
                    "Not found Message: " + id
            );
        }

        existingMessage.update(content);
    }

    // Delete
    @Override
    public void delete(UUID id) {
        Message removedMessage = data.remove(id);

        if (removedMessage == null) {
            throw new IllegalArgumentException(
                    "Message not found: " + id
            );
        }
    }
}
