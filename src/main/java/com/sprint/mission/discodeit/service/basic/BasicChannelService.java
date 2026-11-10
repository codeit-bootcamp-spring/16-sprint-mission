package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelCreateRequestException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelUpdateRequestException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelModificationNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelInfo createChannel(ChannelCreateRequest request) {

        if (request.type() == ChannelType.PRIVATE
                && request.userList() != null
                && !request.userList().isEmpty()
                && request.name() == null
                && request.description() == null) {

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

            return this.toChannelInfo(channel);

        } else if (request.type() == ChannelType.PUBLIC
                && request.userList() == null
                && request.name() != null
                && !request.name().isBlank()) {

            Channel channel = new Channel(request);
            channelRepository.save(channel);

            return this.toChannelInfo(channel);
        } else {
            throw new InvalidChannelCreateRequestException(
                    "옳바르지 않은 채널 생성 요청입니다."
            );
        }
    }

    private Instant findLastMessageAt(UUID channelId) {
        return this.messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);

    }

    private ChannelInfo toChannelInfo(Channel channel) {
        if (channel.getType() == ChannelType.PRIVATE) {
            Set<UUID> participantIds =
                    this.readStatusRepository.findAll().stream()
                    .filter(rs ->
                            rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toSet());

            return new PrivateChannelInfo(
                    channel,
                    this.findLastMessageAt(channel.getId()),
                    participantIds
            );
        } else {
            return new PublicChannelInfo(
                    channel,
                    this.findLastMessageAt(channel.getId())
            );
        }
    }

    @Override
    public ChannelInfo find(ChannelFindRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() ->
                        new ChannelNotFoundException(
                    "ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다."
                ));

        return toChannelInfo(channel);
    }


    public List<ChannelInfo> findAllByUserId(UUID userId) {
        this.userRepository.existsById(userId);

        List<ChannelInfo> availablePrivateChannelsByUserId =
                this.readStatusRepository.findAll().stream()
                        .filter(rs ->
                                rs.getUserId().equals(userId))
                        .distinct()
                        .map(rs ->
                                this.channelRepository.findById(
                                        rs.getChannelId())
                                .orElseThrow(() ->
                                        new ChannelNotFoundException(
                                        "ID가 "
                                        + rs.getChannelId()
                                        + "인 채널을 찾을 수 없습니다."
                                )))
                        .filter(c ->
                                c.getType() == ChannelType.PRIVATE)
                        .map(this::toChannelInfo)
                        .toList();

        List<ChannelInfo> publicChannelsByUserId =
                this.channelRepository.findAll().stream()
                .filter(c -> c.getType().equals(ChannelType.PUBLIC))
                .map(this::toChannelInfo)
                .toList();

        return Stream.concat(availablePrivateChannelsByUserId.stream(),
                publicChannelsByUserId.stream()).toList();
    }

    @Override
    public ChannelInfo update(UUID channelId, ChannelUpdateRequest request) {
        if (request.newName() != null && request.newName().isBlank()) {
            throw new InvalidChannelUpdateRequestException(
                    "채널 이름은 공백으로만 구성할 수 없습니다."
            );
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(
                        "ID가 " + channelId + "인 채널을 찾을 수 없습니다."
                ));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelModificationNotAllowedException(
                    "프라이빗 채널은 수정할 수 없습니다."
            );
        }

        channel.update(request.newName(), request.newDescription());
        this.channelRepository.save(channel);

        return this.toChannelInfo(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new ChannelNotFoundException(
                                "ID가 " + channelId + "인 채널을 찾을 수 없습니다."
                        ));

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
