package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachmentRequests) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        if (!channel.hasUser(request.authorId())) {
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }

        List<UUID> attachmentIds = attachmentRequests.stream()
                .map(a -> binaryContentRepository.save(
                        new BinaryContent(a.fileName(), a.contentType(), a.bytes())))
                .map(BinaryContent::getId)
                .toList();

        Message message = messageRepository.save(
                new Message(request.authorId(), request.channelId(), request.content(), attachmentIds));

        author.addMessage(message.getId());
        userRepository.save(author);

        channel.addMessage(message.getId());
        channelRepository.save(channel);

        return messageMapper.toDto(message);
    }

    @Override
    public MessageDto findById(UUID id) {
        Message message = getMessageOrThrow(id);
        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageDto> findAll() {
        return messageRepository.findAll().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(messageMapper::toDto)
                .toList();
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(messageMapper::toDto)
                .toList();
    }

    @Override
    public MessageDto update(UUID id, MessageUpdateRequest request) {
        Message message = getMessageOrThrow(id);

        requireSender(message, request.requesterId(), "메시지 수정 권한이 없습니다.");
        message.update(request.newContent());
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Message message = getMessageOrThrow(id);
        requireSender(message, requesterId, "메시지 삭제 권한이 없습니다.");

        message.getAttachmentIds().forEach(binaryContentRepository::deleteById);

        userRepository.findById(message.getAuthorId()).ifPresent(author -> {
            author.removeMessage(message.getId());
            userRepository.save(author);
        });

        channelRepository.findById(message.getChannelId()).ifPresent(channel -> {
            channel.removeMessage(message.getId());
            channelRepository.save(channel);
        });

        messageRepository.deleteById(id);
    }

    private Message getMessageOrThrow(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
    }

    private void requireSender(Message message, UUID requesterId, String errorMessage) {
        if (!message.getAuthorId().equals(requesterId)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
