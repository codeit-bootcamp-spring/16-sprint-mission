package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.*;

import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    public FileMessageService(MessageRepository messageRepository,
                              UserRepository userRepository,
                              ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message save(Message message) {
        if (message == null || message.getId() == null) {
            throw new NullPointerException("데이터가 이상함");
        }
        if (message.getUserId() == null || message.getChannelId() == null) {
            throw new NullPointerException("데이터가 이상함");
        }
        if (userRepository.findId(message.getUserId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다: " + message.getUserId());
        }
        if (channelRepository.findId(message.getChannelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다: " + message.getChannelId());
        }

        messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> findId(UUID id) {
        Message message = messageRepository.findId(id);
        if (message == null) {
            return Collections.emptyList();
        }
        List<Message> result = new ArrayList<>();
        result.add(message);
        return result;
    }

    @Override
    public List<Message> findUserlId(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.findId(id);
        if (message == null) {
            throw new NullPointerException("해당메시지 없음");
        }
        message.setContent(content);
        messageRepository.save(message); // 수정
        return message;
    }

    @Override
    public void deleteId(UUID id) {
        messageRepository.deleteId(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {

        List<Message> targets = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());

        for (Message msg : targets) {
            messageRepository.deleteId(msg.getId());
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<Message> targets = messageRepository.findAll().stream()
                .filter(message -> message.getUserId().equals(userId))
                .collect(Collectors.toList());

        for (Message msg : targets) {
            messageRepository.deleteId(msg.getId());
        }
    }
}
