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

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(String comment, UUID userId, UUID channelId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        Channel channel = channelRepository.findById(channelId).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        Message message = new Message(comment, user, channel);

        user.getMessages().add(message);
        channel.getMessages().add(message);

        userRepository.save(user);
        channelRepository.save(channel);
        return messageRepository.save(message);
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
        Message message = findById(id);

        User user = userRepository.findById(message.getUser().getId()).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        Channel channel = channelRepository.findById(message.getChannel().getId()).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        message.update(comment);

        for(Message userMessage : user.getMessages()){
            if(userMessage.getId().equals(id)){
                userMessage.update(comment);
            }
        }

        for(Message channelMessage : channel.getMessages()){
            if(channelMessage.getId().equals(id)){
                channelMessage.update(comment);
            }
        }

        userRepository.save(user);
        channelRepository.save(channel);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);

        User user = userRepository.findById(message.getUser().getId()).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        Channel channel = channelRepository.findById(message.getChannel().getId()).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        user.getMessages().removeIf(m -> m.getId().equals(id));
        channel.getMessages().removeIf(m -> m.getId().equals(id));

        userRepository.save(user);
        channelRepository.save(channel);
        messageRepository.deleteById(id);
    }
}