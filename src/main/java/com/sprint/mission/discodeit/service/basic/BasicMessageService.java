package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public Message create(String comment, UUID userId, UUID channelId) {

        userRepository.findById(userId).orElseThrow(()->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        channelRepository.findById(channelId).orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        Message message = new Message(comment, userId, channelId);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String comment) {

        Message message =  messageRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
        userRepository.findById(message.getAuthorId()).orElseThrow(()->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        channelRepository.findById(message.getChannelId()).orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        message.update(comment);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message =  messageRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
        userRepository.findById(message.getAuthorId()).orElseThrow(()->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        channelRepository.findById(message.getChannelId()).orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        messageRepository.deleteById(id);
    }
}