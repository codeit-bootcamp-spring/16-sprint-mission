package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request, List<MultipartFile> attachments) {
        userRepository.findById(request.authorId()).orElseThrow(() ->
                new ResourceNotFoundException("유저를 찾을 수 없음"));
        channelRepository.findById(request.channelId()).orElseThrow(() ->
                new ResourceNotFoundException("채널을 찾을 수 없음"));

        List<UUID> attachmentIds = new ArrayList<>();

        if (attachments != null) {
            List<BinaryContent> files = new ArrayList<>();

            for (MultipartFile attachment : attachments) {
                if (attachment.isEmpty()) {
                    continue;
                }

                try {
                    files.add(new BinaryContent(
                            attachment.getOriginalFilename(),
                            attachment.getContentType(),
                            attachment.getBytes()));
                } catch (IOException e) {
                    throw new UncheckedIOException("첨부파일을 읽을 수 없음", e);
                }
            }

            for (BinaryContent file : files) {
                binaryContentRepository.save(file);
                attachmentIds.add(file.getId());
            }
        }

        Message message = new Message(
                request.comment(), request.authorId(), request.channelId(), attachmentIds);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("메시지를 찾을 수 없음"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        channelRepository.findById(channelId).orElseThrow(() ->
                new ResourceNotFoundException("채널을 찾을 수 없음"));

        List<Message> messages = messageRepository.findAll();
        return messages.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(MessageUpdateRequest request) {
        Message message = findById(request.messageId());

        userRepository.findById(message.getAuthorId()).orElseThrow(() ->
                new ResourceNotFoundException("유저를 찾을 수 없음"));
        channelRepository.findById(message.getChannelId()).orElseThrow(() ->
                new ResourceNotFoundException("채널을 찾을 수 없음"));

        message.update(request.comment());
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);

        channelRepository.findById(message.getChannelId()).orElseThrow(() ->
                new ResourceNotFoundException("채널을 찾을 수 없음"));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(id);
    }
}