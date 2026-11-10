package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.messages.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messages.MessageUpdateRequest;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.exceptions.FileProcessingException;
import com.sprint.mission.discodeit.exceptions.ForbiddenOperationException;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    public MessageDto createMessage(MessageCreateRequest request,
                                    List<MultipartFile> attachments) {

        // 작성자가 실제로 존재하는지 확인한다.
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 메시지가 작성될 채널이 존재하는지 확인한다.
        Channel channel = channelRepository.findByChannelId(request.channelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("채널을 찾을 수 없습니다"));

        // PRIVATE 채널은 참여자만 메시지를 보낼 수 있습니다.
        if (channel.getType() == ChannelType.PRIVATE
                && !channel.hasMember(request.userId())) {

            throw new ForbiddenOperationException(
                    "비공개 채널 참여자만 메시지를 보낼 수 있습니다."
            );
        }

        // 첨부파일이 전달된 경우 BinaryContent로 변환하여 저장한다.
        List<UUID> attachmentIds = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile attachment : attachments) {

                if (attachment == null || attachment.isEmpty()) {
                    continue;
                }

                BinaryContent binaryContent = createBinaryContent(attachment);

                binaryContentRepository.save(binaryContent);
                attachmentIds.add(binaryContent.getId());
            }
        }

        // 메시지 생성 및 저장
        Message message = new Message(
                request.content(),
                user.getId(),
                channel.getId(),
                attachmentIds
        );

        // Repository에 메시지 저장을 요청한다.
        messageRepository.save(message);
        return toDto(message);
    }

    // MultipartFile을 BinaryContent 객체로 변환한다.
    private BinaryContent createBinaryContent(MultipartFile file) {
        try {
            return new BinaryContent(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );
        } catch (IOException ioException) {
            throw new FileProcessingException(
                    "첨부파일을 읽을 수 없습니다.",
                    ioException
            );
        }
    }

    @Override
    public MessageDto readMessage(UUID id) {

        // ID에 해당하는 메시지를 조회하고, 없으면 예외를 발생시킨다.
        Message message = messageRepository.findByMessageId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("메시지를 찾을 수 없습니다"));

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
    public List<MessageDto> findAllByChannelId(UUID channelId) {

        // 조회 대상 채널이 존재하는지 확인
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

        // 요청자가 실제로 존재하는지 확인한다.
        userRepository.findByUserId(request.requesterId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("요청자를 찾을 수 없습니다."));

        // 메시지 작성자 권한 확인
        if (!targetMessage.getUserId().equals(request.requesterId())) {
            throw new ForbiddenOperationException("메시지 작성자만 수정할 수 있습니다.");
        }

        // 기존 메시지 내용과 동일한지 확인
        if (Objects.equals(targetMessage.getContent(), request.content())) {
            throw new IllegalArgumentException("기존 메시지 내용과 동일합니다.");
        }

        // 메시지 내용 변경 및 저장
        targetMessage.changeContent(request.content());
        messageRepository.save(targetMessage);

        return toDto(targetMessage);
    }

    @Override
    public void deleteMessage(UUID id, UUID requesterId) {

        // 삭제 대상 메시지 조회
        Message targetMessage = messageRepository.findByMessageId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("삭제할 메시지를 찾을 수 없습니다."));

        // 삭제 요청자가 실제로 존재하는지 확인한다.
        userRepository.findByUserId(requesterId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("요청자를 찾을 수 없습니다."));

        // 메시지 작성자와 삭제 요청자가 일치하는지 확인
        if (!targetMessage.getUserId().equals(requesterId)) {
            throw new ForbiddenOperationException("메시지 작성자만 삭제할 수 있습니다.");
        }

        // 메시지에 연결된 첨부파일 삭제
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