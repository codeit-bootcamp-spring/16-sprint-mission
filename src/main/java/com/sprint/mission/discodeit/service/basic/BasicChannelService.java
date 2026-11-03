package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPublicChannel(PublicChannelCreateDto dto) {
        Channel channel = new Channel(ChannelType.PUBLIC, dto.name(), dto.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateDto dto) {
        Channel channel = new Channel(ChannelType.PRIVATE,null,null);
        Channel saveChannel= channelRepository.save(channel);

        if (dto.userIds() != null) {
            Instant now = Instant.now();

            for (UUID userId : dto.userIds()) {
                ReadStatus readStatus = new ReadStatus(userId, saveChannel.getId(), now);
                readStatusRepository.save(readStatus);
            }
        }

        return saveChannel;
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        List<UUID> userIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            userIds = readStatusRepository.findAll().stream()
                    .filter(r -> r.getChannelId().equals(channelId))
                    .map(r->r.getUserId())
                    .toList();
        }

        return ChannelResponseDto.from(channel, lastMessageAt, userIds);
    }


    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();

        List<UUID> joinedPrivateChannelIds = readStatusRepository.findAll().stream()
                .filter(r -> r.getUserId() != null && r.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .toList();
        return allChannels.stream()
                // PUBLIC 채널이거나, 유저가 참여한 PRIVATE 채널인 경우
                .filter(channel -> channel.getType() == ChannelType.PUBLIC
                        || (channel.getType() == ChannelType.PRIVATE && joinedPrivateChannelIds.contains(channel.getId())))
                .map(channel -> {
                    // 최근 시간정보
                    Instant lastMessageAt = messageRepository.findAll().stream()
                            .filter(m -> m.getChannelId().equals(channel.getId()))
                            .map(Message::getCreatedAt)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    //PRIVATE 채널인 경우 참여한 유저들의 ID 리스트
                    List<UUID> userIds = null;
                    if (channel.getType() == ChannelType.PRIVATE) {
                        userIds = readStatusRepository.findAll().stream()
                                .filter(r -> r.getChannelId().equals(channel.getId()))
                                .map(ReadStatus::getUserId)
                                .toList();
                    }

                   //DTO 객체로 조립하여 반환
                    return ChannelResponseDto.from(channel, lastMessageAt, userIds);
                })
                .toList();
    }

    @Override
    public Channel update(ChannelUpdateDto dto) {
        Channel channel = channelRepository.findById(dto.id())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + dto.id() + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(dto.name(), dto.description());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        //관련 메세지 삭제
        messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .forEach(message -> messageRepository.deleteById(message.getId()));
        //ReadStatus 삭제
        readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .forEach(readStatusRepository::delete);
        //최종 대상 채널 삭제
        channelRepository.deleteById(channelId);
    }
}
