package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(
            UserService userService,
            ChannelService channelService
    ) {
        this.messageRepository = new JCFMessageRepository();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        if (authorId == null) {
            throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        userService.findById(authorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다."));

        channelService.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        Message message = new Message(content, authorId, channelId);

        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        message.update(content);

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        messageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        messageRepository.deleteById(id);
    }
}