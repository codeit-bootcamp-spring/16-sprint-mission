package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(
            UserService userService,
            ChannelService channelService
    ) {
        this.messageRepository = new FileMessageRepository();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        if (authorId == null || channelId == null) {
            throw new IllegalArgumentException("작성자와 채널 정보는 필수입니다.");
        }

        userService.findById(authorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        channelService.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

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