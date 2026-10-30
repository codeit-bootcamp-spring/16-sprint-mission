package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) { // DI 의존성 주입
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(Message message) {
        userService.findById(message.getAuthorId());                        // 발신자 존재
        Channel channel = channelService.findById(message.getChannelId());  // 채널 존재
        if (!channel.hasUser(message.getAuthorId())) {                      // 채널 멤버인지 검증 /
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }

        data.put(message.getId(), message);
        userService.addMessage(message.getAuthorId(), message.getId());
        channelService.addMessage(message.getChannelId(), message.getId());
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
        return data.values().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public Message update(UUID id, UUID requesterId, String content) {
        Message message = findById(id);
        requireSender(message, requesterId, "메시지 수정 권한이 없습니다.");
        message.update(content);
        return message;
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Message message = findById(id);

        requireSender(message, requesterId, "메시지 삭제 권한이 없습니다.");
        data.remove(id);
        userService.removeMessage(message.getAuthorId(), id);
        channelService.removeMessage(message.getChannelId(), id);
    }

    private void requireSender(Message message, UUID requesterId, String errorMessage) {
        if(!message.getAuthorId().equals(requesterId)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
