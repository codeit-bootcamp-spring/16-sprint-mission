package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> messages = new ArrayList<>();

    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService,
                             ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage (String content, UUID userId, UUID channelId) {
        User user = userService.findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        Channel channel = channelService.findById(channelId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        Message message = new Message(content, user, channel);
        userService.addMessageToUser(userId, message);
        channelService.addMessageToChannel(channelId, message);
        messages.add(message);
        return message;
    }

    @Override
    public Optional<Message> findMessageById(UUID id) {
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return messages;
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = findMessageById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 Id 입니다.")
        );
        message.update(content);
        return message;
    }

    @Override
    public void deleteMessage (UUID id) {
        Message message = findMessageById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
        );
        User user = message.getUser();
        Channel channel = message.getChannel();
        userService.removeMessageFromUser(user.getId(), message);
        channelService.removeMessageFromChannel(channel.getId(), message);
        messages.remove(message);
    }

    @Override
    public void deleteByUserId (UUID userId) {
        // removeIf(조건에 맞으면 삭제 후 true 반환 / 삭제한게 없다면 false 반환)
        // message의 유저의 아이디가 userId와 같다면 삭제
        messages.removeIf(message ->
                message.getUser().getId().equals(userId));
    }

    @Override
    public void deleteByChannelId (UUID channelId) {
        messages.removeIf(message ->
                message.getChannel().getId().equals(channelId));
    }
}
