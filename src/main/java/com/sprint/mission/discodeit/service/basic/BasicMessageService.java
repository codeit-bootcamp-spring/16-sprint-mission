package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message create(UUID channelId, UUID senduser, String content) {
        if(userRepository.findById(senduser) == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다: " + senduser);
        }
        if(channelRepository.findById(channelId) == null) {
            throw new RuntimeException("존재하지 않는 채널입니다: " + channelId);
        }

        Message message = new Message(channelId, senduser, content);
        return messageRepository.save(message);
    }

    @Override
    public Message read(UUID id) {

        return messageRepository.findById(id);
    }

    @Override
    public List<Message> readAll() {

        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByContent(String keyword) {
        return messageRepository.findByContent(keyword);
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new NoSuchElementException("사용하지 않는 메세지입니다: " + id);
        }
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public Message toggleBookmark(UUID id) {
        Message message = messageRepository.findById(id);
        if(message == null) {
            throw new NoSuchElementException("존재하지 않는 메세지입니다:" + id);
        }
        message.toggleBookmark();
        return messageRepository.save(message);
    }


    @Override
    public void delete(UUID Id) {
        messageRepository.deleteById(Id);
    }

}