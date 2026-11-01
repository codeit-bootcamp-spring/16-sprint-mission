package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 메시지 생성
    @Override
    public MessageDto create(MessageCreateRequest request) {

        // 채널 존재 여부 확인
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException(
                    "Channel not found with id " + request.getChannelId()
            );
        }

        // 작성자 존재 여부 확인
        if (!userRepository.existsById(request.getAuthorId())) {
            throw new NoSuchElementException(
                    "Author not found with id " + request.getAuthorId()
            );
        }

        Message message = new Message(
                request.getContent(),
                request.getChannelId(),
                request.getAuthorId()
        );

        messageRepository.save(message);

        // 첨부 파일 연결
        if (request.getAttachmentIds() != null) {
            attachFiles(
                    request.getAttachmentIds(),
                    request.getAuthorId(),
                    message.getId()
            );
        }

        return toDto(message);
    }

    // 메시지 단건 조회
    @Override
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Message with id " + messageId + " not found"
                ));

        return toDto(message);
    }

    // 전체 메시지 조회
    @Override
    public List<MessageDto> findAll() {
        return messageRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // 특정 채널의 메시지 조회
    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException(
                    "Channel not found with id " + channelId
            );
        }

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toDto)
                .toList();
    }

    // 메시지 수정
    @Override
    public MessageDto update(MessageUpdateRequest request) {

        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Message with id " + request.getMessageId() + " not found"
                ));

        message.update(request.getContent());

        // attachmentIds가 null이 아니면 첨부 파일 교체
        if (request.getAttachmentIds() != null) {

            List<BinaryContent> newAttachments =
                    binaryContentRepository.findAllByIdIn(
                            request.getAttachmentIds()
                    );

            if (newAttachments.size() != request.getAttachmentIds().stream()
                    .distinct().count()) {
                throw new NoSuchElementException(
                        "One or more attachments not found"
                );
            }

            // 기존 첨부 파일 삭제
            binaryContentRepository.findAllByMessageId(message.getId())
                    .forEach(content ->
                            binaryContentRepository.deleteById(content.getId())
                    );

            // 새로운 첨부 파일 연결
            attachFiles(
                    request.getAttachmentIds(),
                    message.getAuthorId(),
                    message.getId()
            );
        }

        messageRepository.save(message);

        return toDto(message);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Message with id " + messageId + " not found"
                ));

        // 연결된 첨부 파일 삭제
        binaryContentRepository.findAllByMessageId(messageId)
                .forEach(content ->
                        binaryContentRepository.deleteById(content.getId())
                );

        // 메시지 삭제
        messageRepository.deleteById(messageId);
    }

    // 첨부 파일을 메시지에 연결
    private void attachFiles(
            List<UUID> attachmentIds,
            UUID userId,
            UUID messageId
    ) {
        List<BinaryContent> attachments =
                binaryContentRepository.findAllByIdIn(attachmentIds);

        if (attachments.size() != attachmentIds.stream().distinct().count()) {
            throw new NoSuchElementException(
                    "One or more attachments not found"
            );
        }

        for (BinaryContent attachment : attachments) {

            if (!userId.equals(attachment.getUserId())) {
                throw new IllegalArgumentException(
                        "You cannot use another user's attachment"
                );
            }

            BinaryContent linkedContent = new BinaryContent(
                    attachment.getFileName(),
                    attachment.getContentType(),
                    attachment.getData(),
                    userId,
                    messageId
            );

            binaryContentRepository.save(linkedContent);
        }
    }

    // Message를 MessageDto로 변환
    private MessageDto toDto(Message message) {

        List<UUID> attachmentIds =
                binaryContentRepository.findAllByMessageId(message.getId())
                        .stream()
                        .map(BinaryContent::getId)
                        .toList();

        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                attachmentIds
        );
    }
}