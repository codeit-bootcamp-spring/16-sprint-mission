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
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        User user = entities.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        Channel channel = entities.getChannels().stream().filter(c -> c.getId().equals(channelId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        Message message = new Message(comment, user, channel);
        entities.getMessages().add(message);
        entities.saveAll();
        return message;
    }

    @Override
    public Message findById(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        return entities.getMessages().stream().filter(m -> m.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
    }

    @Override
    public List<Message> findAll() {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        return entities.getMessages();
    }

    @Override
    public Message update(UUID id, String comment) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        Message message = entities.getMessages().stream().filter(m -> m.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        entities.getUsers().stream().filter(u -> u.getId().equals(message.getUser().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        entities.getChannels().stream().filter(c -> c.getId().equals(message.getChannel().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        message.update(comment);
        entities.saveAll();
        return message;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        Message message = entities.getMessages().stream().filter(m -> m.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        entities.getUsers().stream().filter(u -> u.getId().equals(message.getUser().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        entities.getChannels().stream().filter(c -> c.getId().equals(message.getChannel().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        entities.getMessages().remove(message);
        entities.saveAll();
    }
}