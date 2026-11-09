package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getContent(),
                message.getId(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }

    @Override
    public MessageResponse create(MessageCreateRequest request, List<MultipartFile> attachments) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new ChannelNotFoundException("ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다.");
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new UserNotFoundException("ID가 " + request.authorId() + "인 사용자를 찾을 수 없습니다.");
        }

        List<UUID> newAttachmentIds = new ArrayList<>();

        if (attachments != null) {
            newAttachmentIds = attachments.stream()
                    .map(a -> {
                        try {
                            return this.binaryContentRepository
                                    .save(new BinaryContent(
                                            a.getOriginalFilename(),
                                            a.getContentType(),
                                            a.getBytes()
                                    ))
                                    .getId();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        Message message = new Message(request.content(), request.channelId(), request.authorId(), newAttachmentIds);
        messageRepository.save(message);

        return this.toResponse(message);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        return this.toResponse(messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다.")));
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {

        if (!this.channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException("ID가 " + channelId + "인 채널을 찾을 수 없습니다.");
        }

        return this.messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(UUID messageId, MessageUpdateRequest request, List<MultipartFile> attachments) {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다."));

        String newContent;

        if (request.newContent() != null && !request.newContent().isBlank()) {
            newContent = request.newContent();
        } else {
            newContent = message.getContent();
        }

        List<UUID> newAttachmentIds = new ArrayList<>();

        if (attachments != null) {
            newAttachmentIds = attachments.stream()
                    .map(a -> {
                        try {
                            return this.binaryContentRepository
                                    .save(new BinaryContent(
                                            a.getOriginalFilename(),
                                            a.getContentType(),
                                            a.getBytes()
                                    ))
                                    .getId();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        message.update(newContent, newAttachmentIds);
        this.messageRepository.save(message);

        return this.toResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다.");
        }

        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new
                        MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다."));

        List<UUID> attachmentIds = message.getAttachmentIds();

        this.binaryContentRepository.findAll().stream()
                .filter(b -> attachmentIds.contains(b.getId()))
                .forEach(b -> this.binaryContentRepository.deleteById(b.getId()));

        messageRepository.deleteById(messageId);
    }
}
