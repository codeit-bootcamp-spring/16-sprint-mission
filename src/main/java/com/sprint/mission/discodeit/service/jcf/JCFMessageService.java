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
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, UUID requesterId) {
        Message message = findById(id);

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }

        if(!message.getSenderId().equals(requesterId)) {
            throw new IllegalArgumentException("메시지 수정 권한이 없습니다.");
        }
        data.put(id, message);
        return message;
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Message message = findById(id);

        if(!message.getSenderId().equals(requesterId)) {
            throw new IllegalArgumentException("메시지 삭제 권한이 없습니다.");
        }

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
