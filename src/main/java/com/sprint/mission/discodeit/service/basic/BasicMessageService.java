package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.MessageRequestDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository, BinaryContentRepository binaryContentRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    private MessageResponseDto toDto(Message message) {
        return new MessageResponseDto
                (message.getId(), message.getContent(), message.getChannelId(), message.getAuthorId(), message.getAttachmentIds());
    }

    @Override
    public MessageResponseDto create(MessageRequestDto messageRequestDto) {
        String content = messageRequestDto.content();
        UUID channelId = messageRequestDto.channelId();
        UUID authorId = messageRequestDto.authorId();
        if (!channelRepository.existsById(messageRequestDto.channelId())) {
            throw new NoSuchElementException("Channel with id " + messageRequestDto.channelId() + " not found");
        }
        if (!userRepository.existsById(messageRequestDto.authorId())) {
            throw new NoSuchElementException("Author with id " + messageRequestDto.authorId() + " not found");
        }

        List<UUID> attachmentIds = new ArrayList<>();
        if (messageRequestDto.attachments() != null) {
            for (BinaryContentRequestDto attachment : messageRequestDto.attachments()) {
                BinaryContent binaryContent = new BinaryContent(
                        attachment.fileName(), attachment.data(), attachment.contentType());
                binaryContentRepository.save(binaryContent);
                attachmentIds.add(binaryContent.getId());
            }
        }

        Message message = new Message(content, channelId, authorId, attachmentIds);
        messageRepository.save(message);

        return toDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return toDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        List<MessageResponseDto> messageResponseDtos;

        return messages.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(UUID messageId, MessageUpdateDto messageUpdateDto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(messageUpdateDto.content());
        messageRepository.save(message);

        return toDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        if (message.getAttachmentIds() != null) {
            message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
        }

        messageRepository.deleteById(messageId);
    }
}
