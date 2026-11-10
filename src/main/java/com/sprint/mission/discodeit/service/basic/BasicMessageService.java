
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDto createMessage(
            MessageCreateRequestDto dto,
            List<MultipartFile> attachments
    ) throws IOException {
        User user = findUserEntity(dto.getUserId());
        Channel channel = findChannelEntity(dto.getChannelId());

        List<UUID> attachmentIds = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                BinaryContent binaryContent =
                        new BinaryContent(
                                attachment.getContentType(),
                                attachment.getBytes()
                        );

                BinaryContent savedAttachment =
                        binaryContentRepository.save(binaryContent);

                attachmentIds.add(savedAttachment.getId());
            }
        }

        Message message = new Message(
                dto.getContent(),
                user.getId(),
                channel.getId(),
                MessageType.USER,
                attachmentIds
        );

        Message savedMessage = messageRepository.save(message);

        user.addMessageId(savedMessage.getId());
        channel.addMessageId(savedMessage.getId());

        userRepository.save(user);
        channelRepository.save(channel);

        return toResponseDto(savedMessage);
    }

    @Override
    public MessageResponseDto findMessage(UUID messageId) {
        Message message = findMessageEntity(messageId);

        return toResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllMessagesByChannelId(UUID channelId) {
        findChannelEntity(channelId);

        return messageRepository.findByChannelId(channelId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public MessageResponseDto updateMessage(
            UUID messageId,
            MessageUpdateRequestDto dto
    ) {
        Message message = findMessageEntity(messageId);

        message.updateContent(dto.getContent());

        Message updatedMessage = messageRepository.save(message);

        return toResponseDto(updatedMessage);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findMessageEntity(messageId);

        User user = findUserEntity(message.getSenderId());
        Channel channel = findChannelEntity(message.getChannelId());

        user.removeMessageId(messageId);
        channel.removeMessageId(messageId);

        userRepository.save(user);
        channelRepository.save(channel);

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(messageId);
    }

    private Message findMessageEntity(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 메시지입니다."));
    }

    private Channel findChannelEntity(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private MessageResponseDto toResponseDto(Message message) {
        return MessageResponseDto.builder()
                .id(message.getId())
                .type(message.getType())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .channelId(message.getChannelId())
                .attachmentIds(message.getAttachmentIds())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}