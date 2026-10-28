package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository,
                               UserRepository userRepository,
                               ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        validateContent(content);
        User user = findUser(userId);
        Channel channel = findChannel(channelId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("채널에 참여한 사용자만 메시지를 작성할 수 있습니다.");
        }

        Message message = messageRepository.save(new Message(content, userId, channelId));

        user.addMessage(message.getId());
        channel.addMessage(message.getId());
        userRepository.save(user);
        channelRepository.save(channel);

        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        validateContent(content);
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        User user = findUser(message.getUserId());
        Channel channel = findChannel(message.getChannelId());

        messageRepository.delete(id);
        user.removeMessage(id);
        channel.removeMessage(id);
        userRepository.save(user);
        channelRepository.save(channel);
    }

    private User findUser(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        return user;
    }

    private Channel findChannel(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        return channel;
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용을 입력해야 합니다.");
        }
    }
}