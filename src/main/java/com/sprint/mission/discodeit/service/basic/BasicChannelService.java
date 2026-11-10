package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
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

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    // 공개 채널 생성
    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {
        Channel channel = new Channel(
                request.getName(),
                request.getDescription()
        );

        channelRepository.save(channel);

        return toDto(channel);
    }

    // 비공개 채널 생성
    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {

        // 참여자 ID 중복 제거
        List<UUID> participantIds = request.getUserIds() == null
                ? List.of()
                : request.getUserIds().stream()
                .distinct()
                .toList();

        // 모든 참여자가 존재하는지 먼저 검증
        for (UUID userId : participantIds) {
            if (!userRepository.existsById(userId)) {
                throw new NoSuchElementException(
                        "User with id " + userId + " not found"
                );
            }
        }

        // 검증이 모두 끝난 후 채널 생성 및 저장
        Channel channel = new Channel();
        channelRepository.save(channel);

        // 참여자별 ReadStatus 생성
        for (UUID userId : participantIds) {
            ReadStatus readStatus = new ReadStatus(
                    userId,
                    channel.getId()
            );

            readStatusRepository.save(readStatus);
        }

        return toDto(channel);
    }

    // 채널 단건 조회
    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Channel with id " + channelId + " not found"
                ));

        return toDto(channel);
    }

    // 모든 채널 조회
    @Override
    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // 특정 사용자가 볼 수 있는 채널 조회
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || readStatusRepository.findByUserIdAndChannelId(
                                userId,
                                channel.getId()
                        ).isPresent()
                )
                .map(this::toDto)
                .toList();
    }

    // 채널 수정
    @Override
    public ChannelDto update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Channel with id " + request.getChannelId() + " not found"
                ));

        // PRIVATE 채널은 수정할 수 없음
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException(
                    "Private channel cannot be updated"
            );
        }

        channel.update(
                request.getName(),
                request.getDescription()
        );

        channelRepository.save(channel);

        return toDto(channel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Channel with id " + channelId + " not found"
                ));

        // 해당 채널의 메시지 삭제
        messageRepository.findAllByChannelId(channelId)
                .forEach(message ->
                        messageRepository.deleteById(message.getId())
                );

        // 해당 채널의 ReadStatus 삭제
        readStatusRepository.findAll().stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId)
                )
                .forEach(readStatus ->
                        readStatusRepository.deleteById(readStatus.getId())
                );

        channelRepository.deleteById(channelId);
    }

    // Channel을 ChannelDto로 변환
    private ChannelDto toDto(Channel channel) {

        // 가장 최근 메시지 시간 조회
        Instant latestMessageAt = messageRepository
                .findAllByChannelId(channel.getId())
                .stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        // 채널 참여자 ID 조회
        List<UUID> participantIds = readStatusRepository.findAll().stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channel.getId())
                )
                .map(ReadStatus::getUserId)
                .toList();

        return new ChannelDto(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                latestMessageAt,
                participantIds
        );
    }
}
