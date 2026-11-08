package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelCreateRequestException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelModificationNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
    private final ReadStatusRepository readStatusRepository;

    /* createPublicChannel()과 createPrivateChannel() 메서드들을 createChannel() 메서드로 통합하고
    // 반환 타입을 Channel이 아닌 DTO로 변경합니다.
    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = new Channel(request);
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(request);
        List<ReadStatus> readStatusList = request.userList().stream()
                .map(u -> new ReadStatus(u.getId(), channel.getId()))
                .toList();

        this.channelRepository.save(channel);
        this.readStatusRepository.saveAll(readStatusList);

        return channel;
    }
     */

    @Override
    public ChannelInfo createChannel(ChannelCreateRequest request) {

        if (request.type() == ChannelType.PRIVATE
                && request.userList() != null
                && !request.userList().isEmpty()
                && request.name() == null
                && request.description() == null) {

            Channel channel = new Channel(request);

            // PRIVATE 채널의 사용자 ID 검증: 전달받은 UUID가 실제 존재하는 사용자인지 확인하는 로직이 필요합니다.

            boolean allUserExist = request.userList().stream()
                    .allMatch(this.userRepository::existsById); // 모든 사용자 ID가 존재하면, true를 반환합니다.

            if (!allUserExist) throw new UserNotFoundException("프라이빗 채널에 추가하려는 사용자 중 존재하지 않는 사용자가 있습니다.");

            List<ReadStatus> readStatusList = request.userList().stream()
                    .map(userId -> new ReadStatus(userId, channel.getId()))
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
            throw new InvalidChannelCreateRequestException("옳바르지 않은 채널 생성 요청입니다.");
        }
    }

    // private 접근 제어자를 public으로 바꾼 뒤, ChannelService 인터페의스의 추상 메서드에 추가합니다.
    // 생각해보니까 안해도 될 듯합니다.
    private Instant findLastMessageAt(UUID channelId) {
        return this.messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    private ChannelInfo toChannelInfo(Channel channel) {
        if (channel.getType() == ChannelType.PRIVATE) {
            Set<UUID> participantIds = this.readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toSet());

            return new PrivateChannelInfo(
                    channel,
                    findLastMessageAt(channel.getId()),
                    participantIds
            );
        } else {
            return new PublicChannelInfo(
                    channel,
                    findLastMessageAt(channel.getId())
            );
        }
    }

    @Override
    public ChannelInfo find(ChannelFindRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ChannelNotFoundException("ID가 " + request.channelId() + "인 채널을 찾을 수 없습니다."));

        return toChannelInfo(channel);
    }

    /*
    @Override
    public List<ChannelInfo> findAll() {
        return this.channelRepository.findAll().stream()
                .map(this::toChannelInfo)
                .toList();
    }
     */

    // 특정 User가 볼 수 있는 Channel 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. findAllByUserId
    // ReadStatus를 사용
    public List<ChannelInfo> findAllByUserId(UUID userId) {
        List<ChannelInfo> availablePrivateChannelsByUserId =
                this.readStatusRepository.findAll().stream()
                        .filter(rs -> rs.getUserId().equals(userId))
                        .map(rs -> this.channelRepository.findById(rs.getChannelId())
                                .orElseThrow(() -> new ChannelNotFoundException("ID가 " + rs.getChannelId() + "인 채널을 찾을 수 없습니다.")))
                        .filter(c -> c.getType() == ChannelType.PRIVATE)
                        .map(this::toChannelInfo)
                        .toList();

        List<ChannelInfo> publicChannelsByUserId = this.channelRepository.findAll().stream()
                .filter(c -> c.getType().equals(ChannelType.PUBLIC))
                .map(this::toChannelInfo)
                .toList();

        return Stream.concat(availablePrivateChannelsByUserId.stream(), publicChannelsByUserId.stream()).toList();
    }

    @Override
    public ChannelInfo update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException("ID가 " + channelId + "인 채널을 찾을 수 없습니다."));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelModificationNotAllowedException("프라이빗 채널은 수정할 수 없습니다.");
        }
        channel.update(request.newName(), request.newDescription());
        this.channelRepository.save(channel);

        return this.toChannelInfo(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new ChannelNotFoundException("ID가 " + channelId + "인 채널을 찾을 수 없습니다."));

        List<Message> messageList = this.messageRepository.findAll();
        messageList.stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .forEach(m -> this.messageRepository.deleteById(m.getId()));

        List<ReadStatus> readStatusList = this.readStatusRepository.findAll();
        readStatusList.stream()
                .filter(rs -> rs.getChannelId().equals(channel.getId()))
                .forEach(rs -> this.readStatusRepository.deleteById(rs.getId()));

        this.channelRepository.deleteById(channelId);
    }
}
