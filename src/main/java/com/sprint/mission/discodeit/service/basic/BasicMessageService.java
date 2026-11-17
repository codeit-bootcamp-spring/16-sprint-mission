package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageListResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
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

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;

    @Override
    public MessageResponse create(
            MessageCreateRequest request,
            List<MultipartFile> attachments) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new ChannelNotFoundException(
                    "ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다."
            );
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new UserNotFoundException(
                    "ID가 " + request.authorId() + "인 사용자를 찾을 수 없습니다."
            );
        }

        List<BinaryContent> binaryContents = createBinaryContents(attachments);

        List<UUID> newAttachmentIds = binaryContents.stream()
                .map(BinaryContent::getId)
                .toList();

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                newAttachmentIds);

        messageRepository.save(message);

        return this.messageMapper.toMessageResponse(message);
    }

    private List<BinaryContent> createBinaryContents (
            List<MultipartFile> attachments
    ) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> binaryContents = attachments.stream()
                .map(a -> {
                    try {
                        return new BinaryContent(
                                a.getOriginalFilename(),
                                a.getSize(),
                                a.getContentType(),
                                a.getBytes()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        binaryContents.forEach(this.binaryContentRepository::save);

        return binaryContents;
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = this.findMessageEntity(messageId);

        return this.messageMapper.toMessageResponse(message);
    }

    private Message findMessageEntity(UUID messageId) {
        return this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "ID가 " + messageId + "인 메시지를 찾을 수 없습니다."
                ));
    }

    @Override
    public MessageListResponse findAllByChannelId(UUID channelId) {

        if (!this.channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(
                    "ID가 " + channelId + "인 채널을 찾을 수 없습니다."
            );
        }

        List<MessageResponse> messageResponses = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this.messageMapper::toMessageResponse)
                .toList();

        return new MessageListResponse(messageResponses);
    }

    @Override
    public MessageResponse update(
            UUID messageId,
            MessageUpdateRequest request,
            List<MultipartFile> attachments) {
        Message message = this.findMessageEntity(messageId);
        String newContent;

        if (request.newContent() != null &&
                !request.newContent().isBlank()) {
            newContent = request.newContent();
        } else {
            newContent = message.getContent();
        }

        List<BinaryContent> binaryContents = createBinaryContents(attachments);
        List<UUID> newAttachmentIds = binaryContents.stream()
                .map(BinaryContent::getId)
                .toList();

        message.update(newContent, newAttachmentIds);
        this.messageRepository.save(message);

        return this.messageMapper.toMessageResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = this.findMessageEntity(messageId);
        List<UUID> attachmentIds = message.getAttachmentIds();

        attachmentIds.forEach(this.binaryContentRepository::deleteById);
        messageRepository.deleteById(messageId);
    }
}
