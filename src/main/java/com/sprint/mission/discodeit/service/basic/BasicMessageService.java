package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
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

import java.util.ArrayList;
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

        if (!channelRepository.existsById(request.getChannelId())) {
            throw new NoSuchElementException(
                    "Channel not found with id " + request.getChannelId()
            );
        }

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

        // 첨부파일 생성 및 저장
        List<UUID> attachmentIds = new ArrayList<>();

        if (request.getAttachments() != null) {
            for (BinaryContentCreateRequest attachmentRequest : request.getAttachments()) {
                BinaryContent attachment = new BinaryContent(
                        attachmentRequest.getFileName(),
                        attachmentRequest.getContentType(),
                        attachmentRequest.getData()
                );

                binaryContentRepository.save(attachment);
                attachmentIds.add(attachment.getId());
            }
        }

        // 메시지에 첨부파일 ID 연결
        message.replaceAttachments(attachmentIds);
        messageRepository.save(message);

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

        // attachmentIds가 null이 아니면 첨부파일 교체
        if (request.getAttachmentIds() != null) {

            List<UUID> newAttachmentIds = request.getAttachmentIds()
                    .stream()
                    .distinct()
                    .toList();

            List<BinaryContent> newAttachments =
                    binaryContentRepository.findAllByIdIn(newAttachmentIds);

            if (newAttachments.size() != newAttachmentIds.size()) {
                throw new NoSuchElementException(
                        "One or more attachments not found"
                );
            }

            // 기존 첨부파일 삭제
            for (UUID oldAttachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(oldAttachmentId);
            }

            // 새로운 첨부파일 ID 연결
            message.replaceAttachments(newAttachmentIds);
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

        // 연결된 첨부파일만 삭제
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(messageId);
    }

    // Message를 MessageDto로 변환
    private MessageDto toDto(Message message) {

        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
}