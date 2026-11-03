package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public MessageResponse create(CreateMessageRequest request) {
        if (request == null
                || request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "메시지 내용을 입력하세요."
            );
        }

        User user = userRepository.findById(request.userId());
        Channel channel =
                channelRepository.findById(request.channelId());

        if (user == null || channel == null) {
            throw new IllegalArgumentException(
                    "사용자 또는 채널이 없습니다."
            );
        }

        if (readStatusRepository.findByUserIdAndChannelId(
                user.getId(), channel.getId()) == null) {
            throw new IllegalArgumentException(
                    "채널 참여자만 메시지를 작성할 수 있습니다."
            );
        }

        List<CreateBinaryContentRequest> attachments =
                request.attachments() == null
                        ? List.of()
                        : request.attachments();

        for (CreateBinaryContentRequest file : attachments) {
            if (file == null
                    || file.fileName() == null
                    || file.fileName().isBlank()
                    || file.contentType() == null
                    || file.contentType().isBlank()
                    || file.bytes() == null) {
                throw new IllegalArgumentException(
                        "첨부파일 정보가 올바르지 않습니다."
                );
            }
        }

        Message message = new Message(
                request.content(),
                user.getId(),
                channel.getId()
        );

        for (CreateBinaryContentRequest file : attachments) {
            BinaryContent binary = binaryContentRepository.save(
                    new BinaryContent(
                            file.fileName(),
                            file.contentType(),
                            file.bytes()
                    )
            );

            message.addAttachment(binary.getId());
        }

        messageRepository.save(message);

        user.addMessage(message.getId());
        userRepository.save(user);

        channel.addMessage(message.getId());
        channelRepository.save(channel);

        return toResponse(message);
    }

    @Override
    public MessageResponse findById(UUID id) {
        return toResponse(requireMessage(id));
    }

    @Override
    public List<MessageResponse> findAllByChannelId(
            UUID channelId
    ) {
        if (channelRepository.findById(channelId) == null) {
            throw new IllegalArgumentException(
                    "채널을 찾을 수 없습니다."
            );
        }

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(UpdateMessageRequest request) {
        if (request == null
                || request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "메시지 내용을 입력하세요."
            );
        }

        Message message = requireMessage(request.id());

        message.update(request.content());

        return toResponse(messageRepository.save(message));
    }

    @Override
    public void delete(UUID id) {
        Message message = requireMessage(id);

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }

        messageRepository.delete(id);

        User author =
                userRepository.findById(message.getUserId());

        if (author != null) {
            author.removeMessage(id);
            userRepository.save(author);
        }

        Channel channel =
                channelRepository.findById(message.getChannelId());

        if (channel != null) {
            channel.removeMessage(id);
            channelRepository.save(channel);
        }
    }

    private Message requireMessage(UUID id) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException(
                    "메시지를 찾을 수 없습니다."
            );
        }

        return message;
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getUserId(),
                message.getChannelId(),
                Set.copyOf(message.getAttachmentIds()),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}