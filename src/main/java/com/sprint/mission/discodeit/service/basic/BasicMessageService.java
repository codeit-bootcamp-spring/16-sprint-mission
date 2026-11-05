package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
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

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageCreateResponse create(MessageCreateRequest createRequest,
                                        MessageAttachmentsCreateRequest attachmentsCreateRequest) {
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

        // 채널 가입 여부 확인
        channel.getUsers()
                .stream()
                .filter(userId -> userId.equals(authorId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("채널에 가입되지 않은 유저입니다."));

        Message message = new Message(authorId, content, channelId);

        if (attachmentsCreateRequest != null) {
            List<BinaryContentCreateRequest> files = attachmentsCreateRequest.files();

            files.forEach(binaryContentCreateRequest -> {
                String fileName = binaryContentCreateRequest.fileName();
                byte[] fileContent = binaryContentCreateRequest.content();

                BinaryContent binaryContent = new BinaryContent(fileName, fileContent);
                this.binaryContentRepository.save(binaryContent);
                message.addAttachment(binaryContent.getId());
            });
        }
        this.messageRepository.save(message);

        return MessageCreateResponse.from(message);
    }

    @Override
    public MessageUpdateResponse update(MessageUpdateRequest updateRequest) {
        UUID id = updateRequest.id();
        String newContent = updateRequest.content();

        Message message = this.messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다."));
        message.validateNotDeleted();

        message.update(newContent);
        this.messageRepository.update(id, message);
        return MessageUpdateResponse.from(message);
    }

    @Override
    public MessageFindResponse findById(UUID id) {
        Message message = this.messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("조회할 메세지가 없습니다."));
        return MessageFindResponse.from(message);
    }

    @Override
    public List<MessageFindResponse> findAllByChannelId(UUID channelId) {
        return this.messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(MessageFindResponse::from)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Message message = this.messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 않습니다."));
        message.validateNotDeleted();

        List<UUID> attachmentIds = new ArrayList<>(new LinkedHashSet<>(message.getAttachmentIds()));
        attachmentIds.forEach(attachmentId -> {
            if (this.binaryContentRepository.findById(attachmentId).isEmpty())
                throw new IllegalStateException("삭제할 메세지의 첨부 파일이 없습니다.");
            this.binaryContentRepository.delete(attachmentId);
            message.removeAttachment(attachmentId);
        });

        this.messageRepository.delete(id);
    }
}
