package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** 메시지 규칙과 참조 검증을 처리하며 필요한 저장소 인터페이스에 저장을 위임합니다. */
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Message create request cannot be null.");
        }

        userRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not Found user id: " + request.authorId()));
        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not Found channel id: " + request.channelId()));

        List<BinaryContent> attachments = new ArrayList<>();
        List<UUID> attachmentIds = new ArrayList<>();
        for (BinaryContentCreateRequest attachmentRequest : request.attachments()) {
            if (attachmentRequest == null) {
                throw new IllegalArgumentException("Attachment request cannot be null.");
            }
            BinaryContent attachment = new BinaryContent(
                    attachmentRequest.fileName(),
                    attachmentRequest.contentType(),
                    attachmentRequest.data()
            );
            attachments.add(attachment);
            attachmentIds.add(attachment.getId());
        }

        Message message = new Message(
                request.content(), request.authorId(), request.channelId(), attachmentIds
        );
        if (messageRepository.existsById(message.getId())) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + message.getId());
        }

        try {
            attachments.forEach(binaryContentRepository::save);
            messageRepository.save(message);
            return message;
        } catch (RuntimeException exception) {
            attachments.forEach(attachment ->
                    binaryContentRepository.deleteById(attachment.getId())
            );
            throw exception;
        }
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found Message id: " + id));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not Found channel id: " + channelId));
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Message update request cannot be null.");
        }
        if (request.messageId() == null) {
            throw new IllegalArgumentException("Message ID cannot be null.");
        }
        Message existingMessage = findById(request.messageId());
        existingMessage.update(request.content());
        messageRepository.save(existingMessage);
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        if (!messageRepository.deleteById(id)) {
            throw new IllegalArgumentException("Message not found: " + id);
        }
        message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
    }
}
