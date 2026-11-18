
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.FileStorageException;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public MessageResponse create(
            CreateMessageRequest request,
            List<MultipartFile> attachments
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "메시지 생성 요청이 없습니다."
            );
        }

        if (request.userId() == null) {
            throw new IllegalArgumentException(
                    "메시지 작성자 ID가 필요합니다."
            );
        }

        if (request.channelId() == null) {
            throw new IllegalArgumentException(
                    "채널 ID가 필요합니다."
            );
        }

        List<MultipartFile> validAttachments =
                attachments == null
                        ? List.of()
                        : attachments.stream()
                        .filter(file ->
                                file != null && !file.isEmpty()
                        )
                        .toList();

        String content = request.content() == null
                ? ""
                : request.content();

        boolean hasContent = !content.isBlank();
        boolean hasAttachments = !validAttachments.isEmpty();

        if (!hasContent && !hasAttachments) {
            throw new IllegalArgumentException(
                    "메시지 내용 또는 첨부파일이 필요합니다."
            );
        }

        User user = requireUser(request.userId());
        Channel channel = requireChannel(request.channelId());

        if (readStatusRepository.findByUserIdAndChannelId(
                request.userId(),
                request.channelId()
        ) == null) {
            throw new IllegalStateException(
                    "채널에 참여한 사용자만 메시지를 작성할 수 있습니다."
            );
        }

        List<BinaryContent> files = validAttachments.stream()
                .map(this::convertAttachment)
                .toList();

        Message message = new Message(
                content,
                request.userId(),
                request.channelId()
        );

        for (BinaryContent file : files) {
            BinaryContent saved = binaryContentRepository.save(file);
            message.addAttachment(saved.getId());
        }

        messageRepository.save(message);

        user.addMessage(message.getId());
        channel.addMessage(message.getId());

        userRepository.save(user);
        channelRepository.save(channel);

        return toResponse(message);
    }

    @Override
    public MessageResponse findById(UUID id) {
        return toResponse(requireMessage(id));
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        requireChannel(channelId);

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .sorted(
                        Comparator.comparing(Message::getCreatedAt)
                )
                .map(this::toResponse)
                .toList();
    }

    @Override
    public MessageResponse update(UpdateMessageRequest request) {
        if (request == null || request.id() == null) {
            throw new IllegalArgumentException(
                    "메시지 수정 요청이 올바르지 않습니다."
            );
        }

        Message message = requireMessage(request.id());

        if (request.content() == null ||
                request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "수정할 메시지 내용을 입력하세요."
            );
        }

        message.update(request.content());

        return toResponse(messageRepository.save(message));
    }

    @Override
    public void delete(UUID id) {
        Message message = requireMessage(id);

        User user = userRepository.findById(message.getUserId());
        Channel channel = channelRepository.findById(
                message.getChannelId()
        );

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }

        messageRepository.delete(id);

        if (user != null) {
            user.removeMessage(id);
            userRepository.save(user);
        }

        if (channel != null) {
            channel.removeMessage(id);
            channelRepository.save(channel);
        }
    }

    private BinaryContent convertAttachment(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "첨부파일이 비어 있습니다."
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "첨부파일 이름이 올바르지 않습니다."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        try {
            return new BinaryContent(
                    fileName,
                    contentType,
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new FileStorageException(
                    "첨부파일을 읽는 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    private Message requireMessage(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "메시지 ID가 필요합니다."
            );
        }

        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new ResourceNotFoundException(
                    "메시지를 찾을 수 없습니다."
            );
        }

        return message;
    }

    private User requireUser(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    private Channel requireChannel(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "채널 ID가 필요합니다."
            );
        }

        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new ResourceNotFoundException(
                    "채널을 찾을 수 없습니다."
            );
        }

        return channel;
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
