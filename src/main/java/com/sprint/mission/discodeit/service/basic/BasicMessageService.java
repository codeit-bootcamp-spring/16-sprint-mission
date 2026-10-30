package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(
            String content,
            UUID userId,
            UUID channelId
    ) {
        return create(
                new CreateMessageRequest(
                        content,
                        userId,
                        channelId,
                        List.of()
                )
        );
    }

    @Override
    public Message create(CreateMessageRequest request) {
        if (request == null
                || request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "메시지 내용을 입력하세요."
            );
        }

        User user = requireUser(request.userId());
        Channel channel = requireChannel(request.channelId());

        if (!channel.getMemberIds().contains(request.userId())) {
            throw new IllegalArgumentException(
                    "채널에 참여한 사용자만 메시지를 작성할 수 있습니다."
            );
        }

        Message message = new Message(
                request.content(),
                request.userId(),
                request.channelId()
        );

        List<CreateBinaryContentRequest> attachments =
                request.attachments() == null
                        ? List.of()
                        : request.attachments();

        for (CreateBinaryContentRequest attachment : attachments) {
            if (attachment == null
                    || attachment.fileName() == null
                    || attachment.fileName().isBlank()
                    || attachment.contentType() == null
                    || attachment.contentType().isBlank()
                    || attachment.bytes() == null) {
                throw new IllegalArgumentException(
                        "첨부파일 정보가 올바르지 않습니다."
                );
            }
        }

        for (CreateBinaryContentRequest attachment : attachments) {
            BinaryContent saved = binaryContentRepository.save(
                    new BinaryContent(
                            attachment.fileName(),
                            attachment.contentType(),
                            attachment.bytes()
                    )
            );

            message.addAttachment(saved.getId());
        }

        messageRepository.save(message);

        user.addMessage(message.getId());
        channel.addMessage(message.getId());

        userRepository.save(user);
        channelRepository.save(channel);

        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException(
                    "메시지를 찾을 수 없습니다."
            );
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        requireChannel(channelId);

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .sorted(
                        Comparator.comparing(Message::getCreatedAt)
                )
                .toList();
    }

    @Override
    public Message update(UUID id, String content) {
        return update(
                new UpdateMessageRequest(id, content)
        );
    }

    @Override
    public Message update(UpdateMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "메시지 수정 정보가 없습니다."
            );
        }

        Message message = findById(request.id());

        if (request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "메시지 내용을 입력하세요."
            );
        }

        message.update(request.content());

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);

        User user = requireUser(message.getUserId());
        Channel channel = requireChannel(message.getChannelId());

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }

        messageRepository.delete(id);

        user.removeMessage(id);
        channel.removeMessage(id);

        userRepository.save(user);
        channelRepository.save(channel);
    }

    private User requireUser(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    private Channel requireChannel(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException(
                    "채널을 찾을 수 없습니다."
            );
        }

        return channel;
    }
}