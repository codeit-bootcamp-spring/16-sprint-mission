package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.response.CreatePrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.FindChannelResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;

    @Override
    public ChannelResponse createPublicChannel(CreatePublicChannelRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        channelRepository.save(channel);
        return ChannelResponse.from(channel);
    }

    @Override
    public CreatePrivateChannelResponse createPrivateChannel(CreatePrivateChannelRequest request) {
        List<UUID> requestedIds = request.userList();
        if (requestedIds == null || requestedIds.isEmpty()
                || requestedIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("유효한 참여자가 필요합니다.");
        }

        // 중복된 참여자는 한 번만 등록
        List<UUID> userUUIDs = requestedIds.stream()
                .distinct()
                .toList();

        // 실제 존재하는 유저인지 검증
        userUUIDs.stream()
                .filter(uuid -> !userRepository.existsById(uuid))
                .findFirst()
                .ifPresent(uuid -> {
                    throw new NoSuchElementException("해당 유저는 존재하지 않습니다.");
                });


        Channel channel = new Channel(ChannelType.PRIVATE, "", "");
        channelRepository.save(channel);
        for (UUID userUUID : userUUIDs) {
            readStatusRepository.save(new ReadStatus(userUUID, channel.getId()));
        }

        return new CreatePrivateChannelResponse(channel.getId(), channel.getType());
    }

    @Override
    public FindChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 채널은 존재하지 않음"));
        Instant lastMessageAt = getLastMessageAt(channelId);

        List<UUID> participantIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = getParticipantIds(channelId);
        }

        return FindChannelResponse.from(channel, lastMessageAt, participantIds);
    }

    @Override
    public List<FindChannelResponse> findAllByUserId(UUID userId) {
        // PUBLIC channel부터 추가
        List<FindChannelResponse> result = new ArrayList<>(channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .map(channel -> FindChannelResponse.from(channel, getLastMessageAt(channel.getId()), null))
                .toList());

        // PRIVATE channel 추가
        readStatusRepository.findAll().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .map(status -> channelRepository.findById(status.getChannelId())
                        .orElseThrow(() -> new NoSuchElementException("해당 id의 채널은 존재하지 않음")))
                .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                .map(channel -> FindChannelResponse.from(channel, getLastMessageAt(channel.getId()), getParticipantIds(channel.getId())))
                .forEach(result::add);

        return result;
    }

    @Override
    public ChannelResponse update(UpdateChannelRequest request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 채널이 없음"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없음");
        }
        channel.update(request.newName(), request.newDescription());
        channelRepository.save(channel);
        return ChannelResponse.from(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 id를 가진 채널이 없음");
        }

        readStatusRepository.findAll()
                .stream()
                .filter(status -> channelId.equals(status.getChannelId()))
                .forEach(status -> readStatusRepository.deleteById(status.getId()));

        messageRepository.findAll()
                .stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .forEach(message -> {
                    message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
                    messageRepository.deleteById(message.getId());
                });

        channelRepository.deleteById(channelId);
    }

    private List<UUID> getParticipantIds(UUID channelId) {
        return readStatusRepository.findAll().stream()
                .filter(status -> channelId.equals(status.getChannelId()))
                .map(ReadStatus::getUserId)
                .toList();
    }

    // 채널의 가장 최근 메시지 시간, 메시지가 없으면 null
    private Instant getLastMessageAt(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }
}
