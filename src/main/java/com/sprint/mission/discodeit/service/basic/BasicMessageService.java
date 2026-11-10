package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(CreateMessageRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("해당 id의 채널이 없음");
        }

        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("해당 id의 유저가 없음");
        }

        // 첨부파일이 없으면 빈 리스트
        List<UUID> attachmentIds = Optional.ofNullable(request.files())
                .orElse(List.of())
                .stream()
                .map(this::createBinaryContent)
                .map(binaryContentRepository::save)
                .map(BinaryContent::getId)
                .toList();

        Message message = new Message(request.content(), request.channelId(),
                request.authorId(), attachmentIds);
        messageRepository.save(message);

        return MessageResponse.from(message);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 메시지를 찾을 수 없음"));

        return MessageResponse.from(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 id의 채널이 없음");
        }

        return messageRepository.findAll().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .map(MessageResponse::from)
                .toList();
    }

    @Override
    public MessageResponse update(UUID messageId, UpdateMessageRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지가 없음"));

        message.update(request.content());
        messageRepository.save(message);
        return MessageResponse.from(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지가 없음"));

        message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
        messageRepository.deleteById(messageId);
    }

    private BinaryContent createBinaryContent(BinaryContentRequest request) {
        return new BinaryContent(request.filename(),
                request.size(), request.contentType(), request.bytes());
    }
}
