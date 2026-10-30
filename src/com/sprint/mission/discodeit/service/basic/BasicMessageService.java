package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

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

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다.")
                );

        Message message = new Message(
                request.content(),
                author.getId(),
                channel.getId()
        );

        List<UUID> attachmentIds = request.attachmentIds() == null
                ? List.of()
                : request.attachmentIds();

        if (attachmentIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("첨부파일 ID는 null일 수 없습니다.");
        }

        for (UUID attachmentId : attachmentIds) {
            BinaryContent attachment = binaryContentRepository.findById(attachmentId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("첨부파일을 찾을 수 없습니다.")
                    );

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
                        new IllegalArgumentException("메시지를 찾을 수 없습니다.")
                );

        message.update(request.content());

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "메시지 ID는 필수입니다.");

        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다.")
                );

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(id);
    }
}