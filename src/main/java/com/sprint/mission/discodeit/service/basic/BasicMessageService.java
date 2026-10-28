package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
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
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );
        Channel channel = channelService.findById(channelId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 채널입니다.")
                );
        boolean joined = channel.getUsers().stream()
                .anyMatch(channelUser -> channelUser.getId().equals(userId));
        if (!joined) {
            throw new IllegalArgumentException("채널에 참여한 사용자만 메세지를 작성할 수 있습니다.");
        }
        Message message = new Message(content, user, channel);
        userService.addMessageToUser(userId, message);
        channelService.addMessageToChannel(channelId, message);
        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> findMessageById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = findMessageById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는  메세지입니다.")
        );

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메세지 내용은 비어있을 수 없습니다.");
        }

        message.update(content);
        messageRepository.save(message);

        User user = userRepository.findById(message.getUser().getId())
                .orElseThrow(() ->
                        new IllegalStateException("메세지 작성자가 존재하지 않습니다."));

        Message userMessage = user.getMessages().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("사용자에게 메세지가 존재하지 않습니다."));

        userMessage.update(content);
        userRepository.save(user);

        Channel channel = channelRepository.findById(message.getChannel().getId())
                .orElseThrow(() ->
                        new IllegalStateException("메세지의 채널이 존재하지 않습니다."));

        Message channelMessage = channel.getMessages().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("채널에 메세지가 존재하지 않습니다."));

        channelMessage.update(content);
        channelRepository.save(channel);

        return message;
    }

    @Override
    public void deleteMessage (UUID id) {
        Message message = findMessageById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 메세지입니다.")
        );
        User user = message.getUser();
        Channel channel = message.getChannel();
        userService.removeMessageFromUser(user.getId(), message);
        channelService.removeMessageFromChannel(channel.getId(), message);
        messageRepository.delete(message);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<UUID> messageIds = messageRepository.findAllByUserId(userId).stream()
                .map(Message::getId)
                .toList();

        messageIds.forEach(this::deleteMessage);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        List<UUID> messageIds = messageRepository.findAllByChannelId(channelId).stream()
                .map(Message::getId)
                .toList();

        messageIds.forEach(this::deleteMessage);
    }
}
