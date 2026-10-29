package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
    public Message add(Message message) {
        userRepository.getById(message.getUserId());

        channelRepository.getById(message.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        messageRepository.save(message);
        return message;
    }

    @Override
    public Message getById(UUID id) {
        return messageRepository.getById(id);
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.getAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.getById(id);
        message.update(content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        messageRepository.delete(id);
    }
}