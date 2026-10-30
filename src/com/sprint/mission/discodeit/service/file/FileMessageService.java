package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentService binaryContentService;

    public FileMessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChannelRepository channelRepository,
            BinaryContentService binaryContentService
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.binaryContentService = binaryContentService;
    }

    @Override
    public Message create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 생성 정보는 필수입니다.");
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        Objects.requireNonNull(request.authorId(), "작성자 ID는 필수입니다.");
        Objects.requireNonNull(request.channelId(), "채널 ID는 필수입니다.");

        UUID authorId = request.authorId();
        UUID channelId = request.channelId();

        userRepository.findById(authorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        Message message = new Message(
                request.content(),
                authorId,
                channelId
        );

        List<BinaryContentCreateRequest> attachments =
                request.attachments() == null
                        ? List.of()
                        : request.attachments();

        if (attachments.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("첨부파일 생성 정보는 null일 수 없습니다.");
        }

        for (BinaryContentCreateRequest attachmentRequest : attachments) {
            BinaryContent attachment = binaryContentService.create(attachmentRequest);
            message.addAttachment(attachment.getId());
        }

        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Objects.requireNonNull(id, "메시지 ID는 필수입니다.");
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "채널 ID는 필수입니다.");
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 수정 정보는 필수입니다.");
        }

        Objects.requireNonNull(request.id(), "메시지 ID는 필수입니다.");

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        Message message = messageRepository.findById(request.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.update(request.content());

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "메시지 ID는 필수입니다.");

        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentService.deleteById(attachmentId);
        }

        messageRepository.deleteById(id);
    }
}