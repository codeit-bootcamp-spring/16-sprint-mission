package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentService binaryContentService;

    // 기존 방식의 메시지 생성
    @Override
    public MessageDto create(MessageCreateRequest request) {
        return create(request, List.of());
    }

    // MultipartFile을 이용한 메시지 생성
    @Override
    public MessageDto create(
            MessageCreateRequest request,
            List<MultipartFile> attachments
    ) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 생성 정보는 필수입니다.");
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        if (request.authorId() == null) {
            throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        }

        if (request.channelId() == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        Message message = new Message(
                request.content(),
                author.getId(),
                channel.getId()
        );

        // 첨부파일이 전달되지 않았다면 빈 목록으로 처리
        List<MultipartFile> files = attachments == null
                ? List.of()
                : attachments;

        // 실제 파일을 저장하고 파일 ID만 메시지에 연결
        for (MultipartFile file : files) {

            if (file == null) {
                throw new IllegalArgumentException(
                        "첨부파일은 null일 수 없습니다."
                );
            }

            // 비어 있는 파일은 건너뜀
            if (file.isEmpty()) {
                continue;
            }

            BinaryContentDto attachment =
                    binaryContentService.create(file);

            message.addAttachment(attachment.id());
        }

        Message savedMessage = messageRepository.save(message);

        return toDto(savedMessage);
    }

    @Override
    public Optional<MessageDto> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        return messageRepository.findById(id)
                .map(this::toDto);
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageDto update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 수정 정보는 필수입니다.");
        }

        if (request.id() == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다.");
        }

        Message message = messageRepository.findById(request.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.update(request.content());

        Message savedMessage = messageRepository.save(message);

        return toDto(savedMessage);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        Message message = messageRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 메시지에 연결된 첨부파일 삭제
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentService.deleteById(attachmentId);
        }

        messageRepository.deleteById(id);
    }

    // Message 엔티티를 MessageDto로 변환
    private MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getAttachmentIds()
        );
    }
}