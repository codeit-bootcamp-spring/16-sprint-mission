package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.request.messageRequest.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messageRequest.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
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
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Channel channel = channelRepository.findByChannelId(request.channelId())
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

        // 작성자가 해당 채널에 참여 중인지 확인한다.
        if (!channel.hasMember(request.userId())){
            throw new IllegalArgumentException
                    ("\'" + user.getUsername() + "\' (이)가 해당 채널에 참여 중이 아닙니다.");
        }

        // 첨부파일 DTO를 Entity로 변환하고 저장한다.
        List<UUID> attachmentIds = null;

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
                request.userId(),
                request.channelId(),
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
                        new IllegalArgumentException("메시지를 찾을 수 없습니다"));

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
                        new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 기존 내용과 수정할 내용이 동일한지 확인한다.
        if (targetMessage.getContent().equals(request.content())){
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
                        new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다."));

        // 메시지 작성자와 삭제 요청자의 일치 여부 확인
        if (!targetMessage.getUserId().equals(requesterId)) {
            throw new IllegalArgumentException("메시지 작성자만 삭제할 수 있습니다.");
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