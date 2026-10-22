package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new LinkedHashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        validateContent(content);

        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("채널에 참여한 사용자만 메시지를 작성할 수 있습니다.");
        }

        Message message = new Message(content, userId, channelId);
        data.put(message.getId(), message);

        user.addMessage(message.getId());
        channel.addMessage(message.getId());
        userService.save(user);

        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        validateContent(content);
        message.update(content);
        data.put(id, message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        User user = userService.findById(message.getUserId());
        Channel channel = channelService.findById(message.getChannelId());

        data.remove(id);
        user.removeMessage(id);
        channel.removeMessage(id);
        userService.save(user);
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용을 입력해야 합니다.");
        }
    }
}