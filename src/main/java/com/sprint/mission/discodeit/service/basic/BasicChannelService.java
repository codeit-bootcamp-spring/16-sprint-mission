package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** 채널 업무 흐름을 처리하고 영속화는 ChannelRepository에 맡깁니다. */
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Channel createPublic(PublicChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("공개 채널 생성 요청은 null일 수 없습니다.");
        }

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name(),
                request.description()
        );

        saveChannel(channel);
        return channel;
    }

    @Override
    public Channel createPrivate(PrivateChannelCreateRequest request) {
        if (request == null
                || request.participantUserIds() == null
                || request.participantUserIds().isEmpty()) {
            throw new IllegalArgumentException("비공개 채널에는 참여자가 필요합니다.");
        }

        // 채널을 저장하기 전에 모든 참여자 ID가 null이 아니고 실제 사용자로 존재하는지 확인합니다.
        for (UUID participantUserId : request.participantUserIds()) {
            if (participantUserId == null) {
                throw new IllegalArgumentException("참여자 ID는 null일 수 없습니다.");
            }
            if (!userRepository.existsById(participantUserId)) {
                throw new IllegalArgumentException(
                        "참여자를 찾을 수 없습니다: " + participantUserId
                );
            }
        }

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        saveChannel(channel);

        // 같은 사용자가 요청 목록에 중복되어도 ReadStatus는 한 번만 만듭니다.
        Instant createdAt = Instant.now();
        request.participantUserIds().stream()
                .distinct()
                .forEach(userId -> readStatusRepository.save(
                        new ReadStatus(userId, channel.getId(), createdAt)
                ));

        return channel;
    }

    private void saveChannel(Channel channel) {
        if (channelRepository.existsById(channel.getId())) {
            throw new IllegalArgumentException("이미 등록된 채널 ID입니다: " + channel.getId());
        }

        channelRepository.save(channel);
    }

    @Override
    public ChannelDto findById(UUID id) {
        Channel channel = findChannelOrThrow(id);
        return toDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        Set<UUID> participatingChannelIds = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || participatingChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public void update(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 수정 요청은 null일 수 없습니다.");
        }

        Channel existingChannel = findChannelOrThrow(request.channelId());

        if (existingChannel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        existingChannel.update(request.name(), request.description());
        channelRepository.save(existingChannel);
    }

    @Override
    public void delete(UUID id) {
        // 없는 채널 ID로 연관 데이터를 지우지 않도록 먼저 채널 존재 여부를 확인합니다.
        Channel channel = findChannelOrThrow(id);

        // 이 채널에 속한 메시지들을 찾아 각각 삭제합니다.
        for (Message message : messageRepository.findAll()) {
            if (message.getChannelId().equals(channel.getId())) {
                messageRepository.deleteById(message.getId());

                // 메시지에 연결된 첨부 콘텐츠도 함께 정리해 고아 파일을 남기지 않습니다.
                for (UUID attachmentId : message.getAttachmentIds()) {
                    binaryContentRepository.deleteById(attachmentId);
                }
            }
        }

        // 이 채널의 참여·읽음 상태들도 찾아 각각 삭제합니다.
        for (ReadStatus readStatus : readStatusRepository.findAll()) {
            if (readStatus.getChannelId().equals(channel.getId())) {
                readStatusRepository.deleteById(readStatus.getId());
            }
        }

        // 연결된 데이터들을 정리한 뒤 마지막으로 채널을 삭제합니다.
        if (!channelRepository.deleteById(channel.getId())) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다: " + id);
        }
    }

    private Channel findChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다: " + id));
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channel.getId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantUserIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findAll().stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channel.getId())
                )
                .map(ReadStatus::getUserId)
                .toList()
                : List.of();

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                lastMessageAt,
                participantUserIds
        );
    }
}
