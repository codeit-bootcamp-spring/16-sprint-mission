package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
    public MessageResponseDto createMessage(MessageCreateRequestDto dto) {

        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        // 첨부 파일 확인
        List<UUID> attachmentIds = dto.getAttachmentIds() == null
                ? List.of()
                : dto.getAttachmentIds();

        for (UUID attachmentId : attachmentIds) {
            binaryContentRepository.findById(attachmentId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("존재하지 않는 첨부 파일입니다."));
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
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 메시지입니다."));

        return toResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllMessagesByChannelId(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        return messageRepository.findByChannelId(channelId)
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public MessageResponseDto updateMessage(
            UUID messageId,
            MessageUpdateRequestDto dto
    ) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 메시지입니다."));

        message.updateContent(dto.getContent());

        Message updatedMessage = messageRepository.save(message);

        return toResponseDto(updatedMessage);
    }

    @Override
    public void deleteMessage(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 메시지입니다."));

        User user = userRepository.findById(message.getSenderId())
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지 작성자를 찾을 수 없습니다."));

        Channel channel = channelRepository.findById(message.getChannelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지가 속한 채널을 찾을 수 없습니다."));

        user.removeMessageId(messageId);
        channel.removeMessageId(messageId);

        userRepository.save(user);
        channelRepository.save(channel);

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(messageId);
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