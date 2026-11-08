package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelMapper channelMapper;


    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelRequestDto privateChannelRequestDto) {
        Channel channel =
                new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        privateChannelRequestDto.userList().forEach(userId ->
                readStatusRepository.save(new ReadStatus(userId, channel.getId()))
        );
        return channelMapper.toResponseDto(channel);
    }

    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelRequestDto publicChannelRequestDto) {
        Channel channel =
                new Channel(ChannelType.PUBLIC, publicChannelRequestDto.name(), publicChannelRequestDto.description());
        channelRepository.save(channel);
        return channelMapper.toResponseDto(channel);
    }


    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository
                .findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        return channelMapper.toResponseDto(channel);
    }

    // 메세지 레포지토리
    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        Set<UUID> joinedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        return channelRepository.findAll().stream()
                .filter(ch -> ch.getType() == ChannelType.PUBLIC ||
                        (ch.getType() == ChannelType.PRIVATE && joinedChannelIds.contains(ch.getId())))
                .map(channelMapper::toResponseDto)
                .toList();

    }


    @Override
    public ChannelResponseDto update(UUID channelId, PublicChannelUpdateDto publicChannelUpdateDto) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("Private channels are not supported");
        } else {
            channel.update(publicChannelUpdateDto.newName(), publicChannelUpdateDto.newDescription());
            channelRepository.save(channel);
        }
        return channelMapper.toResponseDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        readStatusRepository.findAllByChannelId(channelId)
                        .forEach(readStatus -> readStatusRepository.deleteById(readStatus.getId()));
        List<Message> messageList = messageRepository.findAllByChannelId(channelId);
        messageList.stream().forEach(message -> {
            if (message.getAttachmentIds() != null) {
                message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
            }
            messageRepository.deleteById(message.getId());
        });

        channelRepository.deleteById(channelId);
    }
}
