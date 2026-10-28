package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

/** 메시지 규칙과 참조 검증을 처리하며 세 저장소 인터페이스에 저장을 위임합니다. */
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    /** 메시지 저장소와 메시지가 참조하는 작성자·채널 저장소를 생성자로 받습니다. */
    public BasicMessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChannelRepository channelRepository
    ) {
        if (messageRepository == null || userRepository == null || channelRepository == null) {
            throw new IllegalArgumentException("Repository dependencies cannot be null.");
        }
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public void create(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        if (messageRepository.existsById(message.getId())) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + message.getId());
        }

        // 메시지가 가리키는 작성자와 채널이 실제 저장되어 있는지 확인합니다.
        userRepository.findById(message.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not Found user id: " + message.getUserId()));
        channelRepository.findById(message.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not Found channel id: " + message.getChannelId()));

        messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Message id: " + id));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public void update(UUID id, String content) {
        Message existingMessage = findById(id);
        existingMessage.update(content);
        messageRepository.save(existingMessage);
    }

    @Override
    public void delete(UUID id) {
        if (!messageRepository.deleteById(id)) {
            throw new IllegalArgumentException("Message not found: " + id);
        }
    }
}
