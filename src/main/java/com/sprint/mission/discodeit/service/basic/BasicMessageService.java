package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest createRequest,
                                  List<MultipartFile> attachments) {
        UUID authorId = createRequest.authorId();
        UUID channelId = createRequest.channelId();
        String content = createRequest.content();

        // 유저와 채널 존재 여부 확인
        User author = this.userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("메세지를 보내는 유저가 존재하지 않습니다."));
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("메세지를 보낼 채널이 존재하지 않습니다."));
        author.validateNotDeleted();
        channel.validateNotDeleted();

        // 채널 가입 여부 확인 - 아직 채널 가입 API는 미사용
//        channel.getUsers()
//                .stream()
//                .filter(userId -> userId.equals(authorId))
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("채널에 가입되지 않은 유저입니다."));

        Message message = new Message(authorId, content, channelId);

        if (attachments != null && !attachments.isEmpty()) {
            attachments.forEach(attachment -> {
                String fileName = attachment.getOriginalFilename();
                Long size = attachment.getSize();
                String contentType = attachment.getContentType();
                byte[] fileContent = null;

                try {
                    fileContent = attachment.getBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                BinaryContent binaryContent = new BinaryContent(fileName, fileContent, size, contentType);
                this.binaryContentRepository.save(binaryContent);
                message.addAttachment(binaryContent.getId());
            });
        }
        this.messageRepository.save(message);

        return MessageResponse.from(message);
    }

    @Override
    public MessageResponse update(UUID id, MessageUpdateRequest updateRequest) {
        String newContent = updateRequest.newContent();

        Message message = this.messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다."));
        message.validateNotDeleted();

        message.update(newContent);
        this.messageRepository.update(id, message);
        return MessageResponse.from(message);
    }

    @Override
    public MessageResponse findById(UUID id) {
        Message message = this.messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("조회할 메세지가 없습니다."));
        return MessageResponse.from(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return this.messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Message message = this.messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 않습니다."));
        message.validateNotDeleted();

        List<UUID> attachmentIds = new ArrayList<>(new LinkedHashSet<>(message.getAttachmentIds()));
        attachmentIds.forEach(attachmentId -> {
            if (this.binaryContentRepository.findById(attachmentId).isPresent()) {
                this.binaryContentRepository.delete(attachmentId);
                message.removeAttachment(attachmentId);
            }
        });

        this.messageRepository.delete(id);
    }
}
