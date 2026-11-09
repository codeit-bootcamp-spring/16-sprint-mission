package com.sprint.mission.discodeit.service.jcf;

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
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.type.ChannelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    // 데이터 및 Repository 의존성
    private final List<Message> data;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 의존성 주입 및 데이터 저장소 초기화
    public JCFMessageService(UserRepository userRepository,
                             ChannelRepository channelRepository,
                             BinaryContentRepository binaryContentRepository) {
        this.data = new ArrayList<>();
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.binaryContentRepository = binaryContentRepository;
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

        data.add(message);      // 메시지를 목록에 추가한다.
        return toDto(message);  // 추가한 메시지를 DTO로 변환하여 반환한다.
    }

    @Override
    public MessageDto readMessage(UUID id) {

        Message targetMessage = data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("메시지를 찾을 수 없습니다"));

        // 조회한 메시지를 DTO로 변환하여 반환
        return toDto(targetMessage);
    }

    @Override
    public List<MessageDto> readAllMessage() {

        // 전체 메시지를 DTO로 변환하여 목록으로 반환
        return data.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {

        // 채널이 실제로 존재하는지 확인한다.
        channelRepository.findByChannelId(channelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("채널을 찾을 수 없습니다."));

        return data.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageDto updateMessage(MessageUpdateRequest request) {

        // 수정할 메시지 조회
        Message targetMessage = data.stream()
                .filter(message -> message.getId().equals(request.messageId()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("메시지를 찾을 수 없습니다."));

        // 메시지 작성자 권한 확인
        if (!targetMessage.getUserId().equals(request.requesterId())) {
            throw new ForbiddenOperationException("메시지 작성자만 수정할 수 있습니다.");
        }

        // 기존 메시지 내용과 동일한지 확인
        if (Objects.equals(targetMessage.getContent(), request.content())) {
            throw new IllegalArgumentException("기존 메시지 내용과 동일합니다.");
        }

        targetMessage.changeContent(request.content()); // 내용이 다를 때만 변경한다.
        return toDto(targetMessage);   // 내용을 변경한 메시지를 DTO로 변환하여 반환한다.
    }

    // 메시지 ID로 조회한 메시지를 삭제
    @Override
    public void deleteMessage(UUID id, UUID requesterId) {
        Message targetMessage = data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("삭제할 메시지를 찾을 수 없습니다."));

        // 메시지 작성자와 삭제 요청자의 일치 여부 확인
        if (!targetMessage.getUserId().equals(requesterId)) {
            throw new ForbiddenOperationException("메시지 작성자만 삭제할 수 있습니다.");
        }

        // 메시지에 연결된 첨부파일도 삭제한다.
        if (targetMessage.getAttachmentIds() != null) {
            for (UUID attachmentId : targetMessage.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }

        // 작성자 확인 후 메시지 목록에서 삭제
        data.remove(targetMessage);
    }

    // Message 객체를 MessageDto로 변환한다.
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