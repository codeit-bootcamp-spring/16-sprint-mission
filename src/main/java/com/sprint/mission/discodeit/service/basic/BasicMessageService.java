package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public Message create(MessageCreateRequest request,List<BinaryContentRequest> attachments) {
        userRepository.findById(request.authorId()).orElseThrow(()->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        channelRepository.findById(request.channelId()).orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        List<UUID> attachmentIds = new ArrayList<>();

        for(BinaryContentRequest binaryContentRequest : attachments){
            BinaryContent binaryContent = new BinaryContent(binaryContentRequest.fileName(),binaryContentRequest.contentType(),binaryContentRequest.bytes());
            binaryContentRepository.save(binaryContent);
            attachmentIds.add(binaryContent.getId());
        }

        Message message = new Message(request.comment(), request.authorId(),request.channelId(),attachmentIds);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAll();
        return messages.stream().filter(m->m.getChannelId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public Message update(MessageUpdateRequest request) {

        Message message =  messageRepository.findById(request.messageId()).orElseThrow(()->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
        userRepository.findById(message.getAuthorId()).orElseThrow(()->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        channelRepository.findById(message.getChannelId()).orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        message.update(request.comment());
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message =  messageRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
        userRepository.findById(message.getAuthorId()).orElseThrow(()->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        channelRepository.findById(message.getChannelId()).orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        for(UUID attachmentId : message.getAttachmentIds()){
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(id);
    }
}