package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) { // DI 의존성 주입
        data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(Message message) {
        isValid(message); // 검증 메서드 통과 x시 예외 발생

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, Message message) {
        if (!data.containsKey(id)) {
            return null;
        }
        data.put(id, message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    private void isValid(Message message) {
        if (userService.findById(message.getSenderId()) == null) {
            throw new IllegalArgumentException("발신자가 존재하지 않습니다.");
        }

        if (userService.findById(message.getReceiverId()) == null) {
            throw new IllegalArgumentException("수신자가 존재하지 않습니다.");
        }

        if (channelService.findById(message.getChannelId()) == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
    }
}
