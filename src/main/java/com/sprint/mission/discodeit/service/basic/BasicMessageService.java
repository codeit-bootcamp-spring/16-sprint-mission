package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final CommonValidator validator;

    @Override
    public MessageDto createMessage(MessageCreateDto dto) {
        validator.validateNotNull(
                dto,
                "메세지 생성 정보는 null일 수 없습니다."
        );
        validator.validateNotBlank(
                dto.content(),
                "메세지 내용은 비어있을 수 없습니다."
        );
        validator.validateNotNull(
                dto.authorId(),
                "작성자 ID는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.channelId(),
                "채널 ID는 null일 수 없습니다."
        );

        userRepository.findById(dto.authorId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "사용자가 존재하지 않습니다."
                        )
                );
        Channel channel = channelRepository.findById(dto.channelId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "채널이 존재하지 않습니다."
                        )
                );

        if (channel.getType() == ChannelType.PRIVATE) {
            boolean joined = readStatusRepository.findAllByUserId(dto.authorId())
                    .stream()
                    .anyMatch(readStatus ->
                            readStatus.getChannelId().equals(dto.channelId())
                    );
            if (!joined) {
                throw new IllegalStateException(
                        "채널에 참여한 사용자만 메세지를 작성할 수 있습니다."
                );
            }
        }

        List<UUID> attachmentIds = new ArrayList<>();
        if (dto.attachments() != null) {
            for (BinaryContentDto attachment : dto.attachments()) {
                BinaryContent binaryContent = new BinaryContent(
                        attachment.filename(),
                        attachment.size(),
                        attachment.bytes()
                );

                BinaryContent saveBinaryContent =
                        binaryContentRepository.save(binaryContent);

                attachmentIds.add(saveBinaryContent.getId());
            }
        }

        Message message = new Message(
                dto.content(),
                dto.authorId(),
                dto.channelId(),
                attachmentIds
        );

        Message savedMessage = messageRepository.save(message);

        return toDto(savedMessage);
    }

    @Override
    public MessageDto findMessageById(UUID id) {
        validator.validateNotNull(
                id,
                "메세지 ID는 null일 수 없습니다."
        );
        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "메세지가 존재하지 않습니다."
                        )
                );
        return toDto(message);
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        validator.validateNotNull(
                channelId,
                "채널 ID는 null일 수 없습니다."
        );
        channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "채널이 존재하지 않습니다."
                        )
                );

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageDto updateMessage(MessageUpdateDto dto) {
        validator.validateNotNull(
                dto,
                "메세지 수정 정보는 null일 수 없습니다."
        );
        validator.validateNotNull(
                dto.id(),
                "메세지 ID는 null일 수 없습니다."
        );

        Message message = messageRepository.findById(dto.id())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "메세지가 존재하지 않습니다."
                        )
                );

        validator.validateNotBlank(
                dto.content(),
                "메세지 내용은 비어있을 수 없습니다."
        );
        message.update(dto.content());

        Message savedMessage = messageRepository.save(message);

        return toDto(savedMessage);
    }

    @Override
    public void deleteMessage(UUID id) {
        validator.validateNotNull(
                id,
                "메세지 ID는 null일 수 없습니다."
        );

        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "메세지가 존재하지 않습니다."
                        )
                );

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.findById(attachmentId)
                    .ifPresent(binaryContentRepository::delete);
        }

        messageRepository.delete(message);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        validator.validateNotNull(
                userId,
                "사용자 ID는 null일 수 없습니다."
        );

        List<UUID> messageIds = messageRepository.findAllByUserId(userId)
                .stream()
                .map(Message::getId)
                .toList();

        messageIds.forEach(this::deleteMessage);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        validator.validateNotNull(
                channelId,
                "채널 ID는 null일 수 없습니다."
        );

        List<UUID> messageIds = messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(Message::getId)
                .toList();

        messageIds.forEach(this::deleteMessage);
    }


    private MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getAttachmentIds()
        );
    }
}
