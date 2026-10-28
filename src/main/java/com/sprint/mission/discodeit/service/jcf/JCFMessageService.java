package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

public JCFMessageService(UserService userService,  ChannelService channelService) {

    this.data =new HashMap<>();
    this.userService = userService;
    this.channelService = channelService;
}

    @Override
    public Message create(UUID channelId, UUID senduser, String content) {
    if(userService.read(senduser)==null) {
        throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + senduser);
    }
        if(channelService.read(channelId)==null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다: " + channelId);
        }

        Message message = new Message(channelId, senduser, content);
        data.put(message.getId(), message);
        return message;
    }
    @Override
    public Message read(UUID id) {
        return data.get(id);
    }
    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }
    @Override
    public List<Message> findByContent(String keyword) {
        return data.values().stream()
                .filter(message -> message.getContent().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 Message입니다." + id);
        }
        message.update(content);
        return message;
    }

    @Override
    public Message toggleBookmark(UUID id) {
        Message message = data.get(id);
            if (message == null) {
                throw new NoSuchElementException("존재하지 않는 Message입니다." + id);
            }
        message.toggleBookmark();
        return message;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

}

