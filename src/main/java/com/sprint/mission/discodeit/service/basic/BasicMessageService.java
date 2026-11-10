package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.mapper.MessageResponseMapper;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentService binaryContentService;



    @Override
    public Message add(Message message) {
        userRepository.getById(message.getUserId());

        channelRepository.getById(message.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        messageRepository.save(message);
        return message;
    }

    @Override
    public Message getById(UUID id) {
        return messageRepository.getById(id);
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.getAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.getById(id);
        message.update(content);
        messageRepository.save(message);
        return message;
    }


    @Override
    public void delete(UUID id) {
        Message message = getById(id);

        messageRepository.delete(id);

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentService.delete(attachmentId);
        }
    }


    @Override
    public MessageResponse find(UUID id) {
        Message message = getById(id);

        return MessageResponseMapper.toResponse(message);
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        if (request == null || request.id() == null) {
            throw new IllegalArgumentException("수정할 메시지 ID가 필요합니다.");
        }

        Message message = update(request.id(), request.content());

        return MessageResponseMapper.toResponse(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID가 필요합니다.");
        }

        return getAll().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .map(MessageResponseMapper::toResponse)
                .toList();
    }

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (request == null
                || request.content() == null
                || request.content().isBlank()
                || request.userId() == null
                || request.channelId() == null) {
            throw new IllegalArgumentException("메시지 정보가 올바르지 않습니다.");
        }

        if (userRepository.getById(request.userId()) == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        channelRepository.getById(request.channelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다.")
                );

        List<UUID> attachmentIds = new ArrayList<>();

        List<BinaryContentCreateRequest> attachments =
                request.attachments() == null
                        ? List.of()
                        : request.attachments();

        try {
            for (BinaryContentCreateRequest attachment : attachments) {
                BinaryContent saved = binaryContentService.create(attachment);
                attachmentIds.add(saved.getId());
            }

            Message message = new Message(
                    request.content(),
                    request.userId(),
                    request.channelId(),
                    attachmentIds
            );

            Message savedMessage = add(message);

            return MessageResponseMapper.toResponse(savedMessage);

        } catch (RuntimeException e) {
            for (UUID attachmentId : attachmentIds) {
                try {
                    binaryContentService.delete(attachmentId);
                } catch (RuntimeException cleanupError) {
                    e.addSuppressed(cleanupError);
                }
            }
            throw e;
        }
    }


}