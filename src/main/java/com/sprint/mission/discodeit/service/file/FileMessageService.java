package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.request.messages.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messages.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.exceptions.ForbiddenOperationException;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.type.ChannelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    // Repository 의존성
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 생성자 의존성 주입
    public FileMessageService(BinaryContentRepository binaryContentRepository,
                              MessageRepository messageRepository,
                              UserRepository userRepository,
                              ChannelRepository channelRepository) {

        this.binaryContentRepository = binaryContentRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public MessageDto createMessage(MessageCreateRequest request) {

        // 작성자와 채널이 실제로 존재하는지 확인한다.
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        Channel channel = channelRepository.findByChannelId(request.channelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("채널을 찾을 수 없습니다."));

        // 비공개 채널인 경우에만 참여 여부를 확인한다.
        if (channel.getType() == ChannelType.PRIVATE
                && !channel.hasMember(request.userId())) {

            throw new ForbiddenOperationException(
                    "비공개 채널 참여자만 메시지를 보낼 수 있습니다."
            );
        }

        // 첨부파일이 전달된 경우 BinaryContent로 변환하여 저장한다.
        List<UUID> attachmentIds = new ArrayList<>();

        // 첨부파일이 전달된 경우 Entity로 변환하고 저장한다.
        if (request.attachments() != null) {
            attachmentIds = request.attachments().stream()
                    .map(attachment -> {
                        BinaryContent binaryContent = new BinaryContent(
                                attachment.fileName(),
                                attachment.contentType(),
                                attachment.size(),
                                attachment.bytes()
                        );

                        binaryContentRepository.save(binaryContent);

                        return binaryContent.getId();
                    })
                    .toList();
        }

        // 메시지 생성 및 저장
        Message message = new Message(
                request.content(),
                user.getId(),
                channel.getId(),
                attachmentIds
        );

        // Repository에 메시지 저장을 요청한 후 DTO로 변환해 반환한다.
        messageRepository.save(message);
        return toDto(message);
    }

    @Override
    public MessageDto readMessage(UUID id) {

        // 요청받은 ID와 일치하는 메시지를 찾아 반환한다.
        Message targetMessage = messageRepository.findByMessageId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("메시지를 찾을 수 없습니다"));

        return toDto(targetMessage);
    }

    @Override
    public List<MessageDto> readAllMessage() {

        // 전체 메시지 목록을 DTO로 변환한다.
        return messageRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {

        // 채널이 실제로 존재하는지 확인한다.
        channelRepository.findByChannelId(channelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("채널을 찾을 수 없습니다."));

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageDto updateMessage(MessageUpdateRequest request) {

        // 수정 대상 메시지 조회
        Message targetMessage = messageRepository.findByMessageId(request.messageId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("메시지를 찾을 수 없습니다"));

        // 메시지 작성자 권한 확인
        if (!targetMessage.getUserId().equals(request.requesterId())) {
            throw new ForbiddenOperationException("메시지 작성자만 수정할 수 있습니다.");
        }

        // 기존 메시지 내용과 동일한지 확인
        if (Objects.equals(targetMessage.getContent(), request.content())) {
            throw new IllegalArgumentException("기존 메시지 내용과 동일합니다.");
        }

        // 메시지 내용을 변경한다.
        targetMessage.changeContent(request.content());

        // 변경된 메시지 정보를 Repository에 저장하고 DTO로 변환하고 반환한다.
        messageRepository.save(targetMessage);
        return toDto(targetMessage);
    }

    // 전달받은 ID와 일치하는 메시지를 찾아 삭제한다.
    @Override
    public void deleteMessage(UUID id, UUID requesterId) {

        // 삭제할 메시지가 존재하는지 확인한다.
        Message targetMessage = messageRepository.findByMessageId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("삭제할 메시지를 찾을 수 없습니다."));

        // 메시지 작성자와 삭제 요청자가 일치하는지 확인한다.
        if (!targetMessage.getUserId().equals(requesterId)) {
            throw new ForbiddenOperationException("메시지 작성자만 삭제할 수 있습니다.");
        }

        // 메시지에 연결된 첨부파일도 삭제한다.
        if (targetMessage.getAttachmentIds() != null) {
            for (UUID attachmentId : targetMessage.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }

        // 메시지 삭제
        messageRepository.deleteByMessageId(id);
    }

    // Message를 MessageDto로 변환한다.
    private MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getContent(),
                message.getUserId(),
                message.getChannelId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}