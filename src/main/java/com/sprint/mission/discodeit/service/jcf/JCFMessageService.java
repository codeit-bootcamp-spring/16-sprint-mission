package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> messages = new ArrayList<>();

    private final UserService userservice;
    private final ChannelService channelService;

    public JCFMessageService(UserService userservice,
                             ChannelService channelService) {
        this.userservice = userservice;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage (String content, UUID userId, UUID channelId) {
        userservice.findById(userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        channelService.findById(channelId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
                );
        Message message = new Message(content,  userId, channelId);
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
        messages.remove(message);
    }
}
