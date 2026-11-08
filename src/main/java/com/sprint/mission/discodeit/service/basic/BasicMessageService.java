package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    /*
    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author not found with id " + authorId);
        }

        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }
     */

    // 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
    // DTO를 활용해 파라미터를 그룹화합니다.

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getContent(),
                message.getId(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }

    @Override
    public MessageResponse create(MessageCreateRequest request, List<MultipartFile> attachments) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new ChannelNotFoundException("ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다.");
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new UserNotFoundException("ID가 " + request.authorId() + "인 사용자를 찾을 수 없습니다.");
        }

        // null보다 List.of()를 사용, 빈 리스트를 사용하면 나중에 첨부파일 목록을 순회할 때 불필요한 null 검사를 줄일 수 있다.
        // List.of()는 수정이 불가능하기 때문에 new ArrayList<>(); 를 사용하는 것이 좋다.
        List<UUID> newAttachmentIds = new ArrayList<>();

        // 이미지 바이너리 데이터(byte[]) 자체에는 일반적으로 파일명과 HTTP Content-Type이 포함되지 않습니다.
        // 파일명은 HTTP Part의 Content-Disposition 헤더에, MIME 타입은 Content-Type 헤더에 포함됩니다.
        // Spring MVC는 업로드된 파일과 메타데이터를 MultipartFile 객체로 제공합니다.
        // getOriginalFilename(), getContentType(), getBytes()로 각각 파일명, MIME 타입, 바이너리 데이터를 가져올 수 있습니다.
        if (attachments != null) {
            newAttachmentIds = attachments.stream()
                    .map(a -> {
                        try {
                            return this.binaryContentRepository
                                    .save(new BinaryContent(
                                            a.getOriginalFilename(),
                                            a.getContentType(),
                                            a.getBytes()
                                    ))
                                    .getId();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }
            /*
            List<BinaryContent> attachments = new ArrayList<>(request.attachments());
            newAttachmentIds = attachments.stream()
                    .map(a -> this.binaryContentRepository
                            .save(new BinaryContent(
                                    a.getFileName(),
                                    a.getContentType(),
                                    a.getBytes()
                            ))
                            .getId())
                    .toList();
             */

        Message message = new Message(request.content(), request.channelId(), request.authorId(), newAttachmentIds);
        messageRepository.save(message);

        return this.toResponse(message);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        return this.toResponse(messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다.")));
    }

    /*
    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }
    */

    // 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByChannelId()
    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {

        if (!this.channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException("ID가 " + channelId + "인 채널을 찾을 수 없습니다.");
        }

        return this.messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(this::toResponse)
                .toList();
    }

    /*
    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        return messageRepository.save(message);
    }
    */

    @Override
    public MessageResponse update(UUID messageId, MessageUpdateRequest request, List<MultipartFile> attachments) {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다."));

        String newContent; // null로 초기화 할 필요가없습니다. 조건문에서 값을 무조건 할당하기 때문입니다.
        // 지역변수는 사용하기 전에 반드시 초기화되어 있어야 합니다.

        if (request.newContent() != null && !request.newContent().isBlank()) {
            newContent = request.newContent();
        } else {
            // MessageUpdateRequest DTO에 문자열이 없을 때, 원래 콘텐츠를 유지합니다.
            newContent = message.getContent();
        }

        // List<BinaryContent> newBinaryContents = new ArrayList<BinaryContent>(); // 난 이것을 왜 쓴것인가??
        // newAttachmentIds는 값이 할당되지 않을 가능성이 있기 때문에 초기화해야합니다.
        List<UUID> newAttachmentIds = new ArrayList<>();

        if (attachments != null) {
            newAttachmentIds = attachments.stream()
                    .map(a -> {
                        try {
                            return this.binaryContentRepository
                                    .save(new BinaryContent(
                                            a.getOriginalFilename(),
                                            a.getContentType(),
                                            a.getBytes()
                                    ))
                                    .getId();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        message.update(newContent, newAttachmentIds);
        this.messageRepository.save(message);

        return this.toResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다.");
        }

        // [ ] 관련된 도메인도 같이 삭제합니다. 첨부파일(BinaryContent)
        // Message를 삭제할 때, 그 메시지의 attachmentId에 해당하는 모든 binaryContent를 삭제한다.

        // 1. 메시지 객체 가져오기.
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new
                        MessageNotFoundException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다."));

        // 2. 메시지의 attachmentIds 가져오기.
        List<UUID> attachmentIds = message.getAttachmentIds();

        // 3. attachmentIds 에 있는 모든 id값을 가지고있는 binaryContent 삭제하기.
        this.binaryContentRepository.findAll().stream()
                .filter(b -> attachmentIds.contains(b.getId()))
                .forEach(b -> this.binaryContentRepository.deleteById(b.getId()));

        messageRepository.deleteById(messageId);
    }
}
