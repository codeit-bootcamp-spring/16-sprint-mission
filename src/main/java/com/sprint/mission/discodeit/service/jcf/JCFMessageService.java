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

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메세지 내용은 비어있을 수 없습니다.");
        }
        User user = userService.findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        Channel channel = channelService.findById(channelId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        boolean joined = channel.getUsers().stream()
                .anyMatch(channelUser -> channelUser.getId().equals(userId));
        if (!joined) {
            throw new IllegalArgumentException("채널에 참여한 사용자만 메세지를 작성할 수 있습니다.");
        }
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
        return List.copyOf(messages);
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = findMessageById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 Id 입니다.")
        );

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메세지 내용은 비어있을 수 없습니다.");
        }

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
        List<UUID> messageIds = messages.stream()
                .filter(message -> // 전달받은 유저의 메세지들만 찾기
                        message.getUser().getId().equals(userId)
                )
                .map(Message::getId) // 해당 메세지 객체의 ID만 가져오기
                .toList();

        messageIds.forEach(this::deleteMessage); // 해당 메세지 객체들 ID를 통해 삭제
    }

    @Override
    public void deleteByChannelId (UUID channelId) {
        List<UUID> messageIds = messages.stream()
                .filter(message ->
                        message.getChannel().getId().equals(channelId)
                )
                .map(Message::getId)
                .toList();

        messageIds.forEach(this::deleteMessage);
    }
}
