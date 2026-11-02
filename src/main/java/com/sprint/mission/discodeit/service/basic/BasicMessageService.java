package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.messageRequest.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service // 해당 클래스를 Spring의 Service 계층 Bean으로 등록하여, Spring IoC 컨테이너가 관리하도록 한다.
@RequiredArgsConstructor // final 필드와 @NonNull 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 생성하도록 한다.
public class BasicMessageService implements MessageService {

    // Repository 의존성
    private final BinaryContentRepository binaryContentRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public MessageDto createMessage(MessageCreateRequest request) {

        // 작성자가 실제로 존재하는지 확인한다.
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 메시지가 작성될 채널이 존재하는지 확인한다.
        Channel channel = channelRepository.findByChannelId(request.channelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다"));

        // 첨부파일이 전달된 경우 존재 여부를 확인한다.
        if (request.attachmentIds() != null) {
            for (UUID attachmentId : request.attachmentIds()) {
                binaryContentRepository.findById(attachmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));
            }
        }

        // 작성자의 채널 참여 여부 확인
        if (!channel.hasMember(request.userId())) {
            throw new IllegalArgumentException(
                    "'" + user.getUsername() + "' (이)가 해당 채널에 참여 중이 아닙니다.");
        }

        // 메시지 생성 및 저장
        Message message = new Message(
                request.content(),
                request.userId(),
                request.channelId(),
                request.attachmentIds()
        );

        // Repository에 메시지 저장을 요청한다.
        messageRepository.save(message);
        return toDto(message);
    }

    @Override
    public MessageDto readMessage(UUID id) {

        // ID에 해당하는 메시지를 조회하고, 없으면 예외를 발생시킨다.
        Message message = messageRepository.findByMessageId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다"));

        return toDto(message);
    }

    @Override
    public List<MessageDto> readAllMessage() {

        // 전체 메시지 목록 조회 및 DTO 변환
        return messageRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDto updateMessage(MessageUpdateRequest request) {

        // 수정 대상 메시지 조회
        Message targetMessage = messageRepository.findByMessageId(request.messageId())
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다"));

        // null 안전성 개선을 위해 기존 메시지 내용과 동일한지 확인
        if (Objects.equals(targetMessage.getContent(), request.content())) {
            throw new IllegalArgumentException("기존 메시지 내용과 동일합니다.");
        }

        // 작성자 권한을 검증하기 위해 메시지 작성자와 수정 요청자가 일치하는지 확인
        if (!targetMessage.getUserId().equals(request.requesterId())) {
            throw new IllegalArgumentException("메시지 작성자만 수정할 수 있습니다.");
        }

        // 메시지 내용 변경 및 저장
        targetMessage.changeContent(request.content());
        messageRepository.save(targetMessage);

        return toDto(targetMessage);
    }

    @Override
    public void deleteMessage(UUID id, UUID requesterId) {

        // 삭제 대상 메시지 조회
        MessageDto targetMessage = readMessage(id);

        // 메시지 작성자와 삭제 요청자가 일치하는지 확인
        if (!targetMessage.userId().equals(requesterId)) {
            throw new IllegalArgumentException("메시지 작성자만 삭제할 수 있습니다.");
        }

        // 메시지에 연결된 첨부파일 삭제
        if (targetMessage.attachmentIds() != null) {
            for (UUID attachmentId : targetMessage.attachmentIds()) {
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