package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelUpdateRequestException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelModificationNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
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

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final ChannelMapper channelMapper;

    /*
    @Override
    public ChannelResponse createChannel(ChannelCreateRequest request) {
        if (this.isPrivateChannel(request)) {
            Channel channel = new Channel(request);

            boolean allUserExist = request.userList().stream()
                    .allMatch(this.userRepository::existsById);

            if (!allUserExist) throw new UserNotFoundException(
                    "프라이빗 채널에 추가하려는 사용자 중 존재하지 않는 사용자가 있습니다."
            );

            List<ReadStatus> readStatusList = request.userList().stream()
                    .distinct()
                    .map(userId ->
                            new ReadStatus(userId, channel.getId()))
                    .toList();

            this.channelRepository.save(channel);
            this.readStatusRepository.saveAll(readStatusList);

            Set<UUID> userIdSet = this.findParticipantIds(channel.getId());


            return this.channelMapper.toChannelResponse(channel, null, userIdSet);

        } else if (this.isPublicChannel(request)) {
            Channel channel = new Channel(request);

            channelRepository.save(channel);

            return this.channelMapper.toChannelResponse(channel, null, null);
        } else {
            throw new InvalidChannelCreateRequestException(
                    "올바르지 않은 채널 생성 요청입니다."
            );
        }
    }
     */

    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
        Channel publicChannel = new Channel(request);
        this.channelRepository.save(publicChannel);

        return this.channelMapper.toChannelDto(publicChannel);
    }

    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        boolean allUserExist = request.participantIds().stream()
                .allMatch(this.userRepository::existsById);

        if (!allUserExist) throw new UserNotFoundException(
                "프라이빗 채널에 추가하려는 사용자 중 존재하지 않는 사용자가 있습니다."
        );

        Channel privateChannel = new Channel(request);

        List<ReadStatus> readStatusList = request.participantIds().stream()
                .distinct()
                .map(userId ->
                        new ReadStatus(userId, privateChannel.getId()))
                .toList();

        this.channelRepository.save(privateChannel);
        this.readStatusRepository.saveAll(readStatusList);

        return this.channelMapper.toChannelDto(privateChannel);
    }

    /*
    private boolean isPrivateChannel(ChannelCreateRequest request) {
        return request.type() == ChannelType.PRIVATE
                && request.userList() != null
                && !request.userList().isEmpty()
                && request.name() == null
                && request.description() == null;
    }

    private boolean isPublicChannel(ChannelCreateRequest request) {
        return request.type() == ChannelType.PUBLIC
                && request.userList() == null
                && request.name() != null
                && !request.name().isBlank();
    }
     */

    private Instant findLastMessageAt(UUID channelId) {
        return this.messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    /*
    @Override
    public ChannelResponse find(ChannelFindRequest request) {
        Channel channel = this.findChannelEntity(request.channelId());
        Instant lastMessageAt = this.findLastMessageAt(request.channelId());
        Set<UUID> userIdSet = null;

        if (channel.getType() == ChannelType.PRIVATE) {
            userIdSet = this.findParticipantIds(request.channelId());
        }

        return this.channelMapper.toChannelResponse(channel, lastMessageAt, userIdSet);
    }
     */

    private Set<UUID> findParticipantIds(UUID channelId) {
        return this.readStatusRepository.findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .map(ReadStatus::getUserId)
                .collect(Collectors.toSet());
    }

    private Channel findChannelEntity(UUID channelId) {
        return this.channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new ChannelNotFoundException(
                                "ID가 " + channelId + "인 채널을 찾을 수 없습니다."
                        ));
    }

    @Override
    public ChannelListResponse findAllByUserId(UUID userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
            );
        }

        // 1. userId를 필드에 가지고 있는 모든 ReadStatus를 가져옵니다.
        List<ReadStatus> readStatusesByUserId =
                this.readStatusRepository.findAllByUserId(userId);

        // 2. ReadStatus 의 ChannelId를 가져옵니다.
        Set<UUID> channelIdsByUserId = readStatusesByUserId.stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        List<ChannelResponse> privateChannelResponses =
                this.channelRepository.findAll().stream()
                        .filter(c -> c.getType() == ChannelType.PRIVATE
                        && channelIdsByUserId.contains(c.getId()))
                        .map(c -> {
                            Channel channel = c;
                            Set<UUID> participantIds = this.findParticipantIds(c.getId());
                            Instant lastMessageAt = this.findLastMessageAt(c.getId());

                            return this.channelMapper.toChannelResponse(
                                    channel,
                                    participantIds,
                                    lastMessageAt);
                        })
                        .distinct()
                        .collect(Collectors.toCollection(ArrayList::new));

        List<ChannelResponse> publicChannelResponses = this.channelRepository.findAll().stream()
                .filter(c -> c.getType() == ChannelType.PUBLIC)
                .distinct()
                .map((c) -> {
                    Channel channel = c;
                    Instant lastMessageAt = this.findLastMessageAt(c.getId());

                    return this.channelMapper.toChannelResponse(channel,
                            null,
                            lastMessageAt);
                })
                .distinct()
                .toList();

        privateChannelResponses.addAll(publicChannelResponses);

        return new ChannelListResponse(privateChannelResponses);
    }

    @Override
    public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {
        if (request.newName() != null && request.newName().isBlank()) {
            throw new InvalidChannelUpdateRequestException(
                    "채널 이름은 공백으로만 구성할 수 없습니다."
            );
        }

        Channel channel = this.findChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelModificationNotAllowedException(
                    "프라이빗 채널은 수정할 수 없습니다."
            );
        }

        channel.update(request.newName(), request.newDescription());

        this.channelRepository.save(channel);

        return this.channelMapper.toChannelDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = this.findChannelEntity(channelId);

        List<Message> messageList = this.messageRepository.findAll();

        messageList.stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .forEach(m ->
                        this.messageRepository.deleteById(m.getId()));

        List<List<UUID>> attachmentIdsList = messageList.stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .map(Message::getAttachmentIds)
                .toList();

        attachmentIdsList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet())
                .forEach(this.binaryContentRepository::deleteById);

        List<ReadStatus> readStatusList = this.readStatusRepository.findAll();

        readStatusList.stream()
                .filter(rs ->
                        rs.getChannelId().equals(channel.getId()))
                .forEach(rs ->
                        this.readStatusRepository.deleteById(rs.getId()));

        this.channelRepository.deleteById(channelId);
    }
}
