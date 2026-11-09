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

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
        if (request == null
                || request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException(
                    "메시지 내용을 입력하세요."
            );
        }

        User user = userRepository.findById(request.userId());

        if (user == null) {
            throw new ResourceNotFoundException(
                    "메시지 작성자를 찾을 수 없습니다."
            );
        }

        Channel channel =
                channelRepository.findById(request.channelId());

        if (channel == null) {
            throw new ResourceNotFoundException(
                    "메시지를 작성할 채널이 없습니다."
            );
        }

        if (readStatusRepository.findByUserIdAndChannelId(
                user.getId(),
                channel.getId()
        ) == null) {
            throw new IllegalStateException(
                    "채널에 참여한 사용자만 메시지를 작성할 수 있습니다."
            );
        }

        List<BinaryContent> convertedAttachments =
                new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                convertedAttachments.add(
                        convertAttachment(attachment)
                );
            }
        }

        Message message = new Message(
                request.content(),
                user.getId(),
                channel.getId()
        );

        for (BinaryContent content : convertedAttachments) {
            BinaryContent savedContent =
                    binaryContentRepository.save(content);

            message.addAttachment(savedContent.getId());
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
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException(
                    "채널 ID가 없습니다."
            );
        }

        if (channelRepository.findById(channelId) == null) {
            throw new ResourceNotFoundException(
                    "채널을 찾을 수 없습니다. ID: " + channelId
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
                    "메시지 수정 내용을 입력하세요."
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

    private BinaryContent convertAttachment(
            MultipartFile attachment
    ) {
        if (attachment == null || attachment.isEmpty()) {
            throw new IllegalArgumentException(
                    "빈 첨부파일은 등록할 수 없습니다."
            );
        }

        String fileName = attachment.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "첨부파일 이름이 없습니다."
            );
        }

        String contentType = attachment.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        try {
            return new BinaryContent(
                    fileName,
                    contentType,
                    attachment.getBytes()
            );
        } catch (IOException exception) {
            throw new FileStorageException(
                    "첨부파일을 읽는 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    private Message requireMessage(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "메시지 ID가 없습니다."
            );
        }

        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new ResourceNotFoundException(
                    "메시지를 찾을 수 없습니다. ID: " + id
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