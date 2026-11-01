package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.request.channelRequest.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channelRequest.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channelRequest.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.type.ChannelType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.*;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    // 의존성 주입 및 데이터 저장소 초기화
    public JCFChannelService(
                    UserRepository userRepository,
                    ReadStatusRepository readStatusRepository,
                    MessageRepository messageRepository) {

        this.data = new ArrayList<>();
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
        this.messageRepository = messageRepository;
    }

    // PUBLIC 채널 생성
    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
        boolean duplicated = data.stream()
                .anyMatch(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                && channel.getName().equals(request.channelName()));

        if (duplicated) {
            throw new IllegalArgumentException("중복된 채널 이름입니다.");
        }

        Channel channel = new Channel(
                request.channelName(), request.description()
        );

        data.add(channel);
        return toDto(channel);
    }

    // PRIVATE 채널 생성
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {

        // 참여자 목록이 비어 있는지 확인
        if (request.memberIds() == null || request.memberIds().isEmpty()) {
            throw new IllegalArgumentException("참여자가 필요합니다.");
        }

        // 모든 참여자의 존재 여부 확인
        for (UUID userId : request.memberIds()) {
            if (userRepository.findByUserId(userId).isEmpty()) {
                throw new IllegalArgumentException("존재하지 않는 유저입니다.");
            }
        }

        // 검증 완료 후 PRIVATE 채널 생성
        Channel channel = new Channel(request.memberIds());

        data.add(channel);

        // 참여자별 ReadStatus 생성 및 저장
        for (UUID userId : channel.getMemberIds()) {
            ReadStatus readStatus = new ReadStatus(
                    userId, channel.getId(), Instant.now());

            readStatusRepository.save(readStatus);
        }

        return toDto(channel);
    }

    @Override
    public ChannelDto readChannel(UUID id) {

        // 요청받은 ID와 일치하는 채널을 찾아 DTO로 반환한다.
        return toDto(findChannel(id));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || channel.hasMember(userId))
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChannelDto updateChannel(ChannelUpdateRequest request) {

        // 수정할 채널 조회
        Channel targetChannel = findChannel(request.channelId());

        // PRIVATE 채널 수정 방지
        if (targetChannel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // 기존 채널명과 변경할 채널명이 같은지 확인
        if (targetChannel.getName().equals(request.channelName())) {
            throw new IllegalArgumentException("기존 채널명과 동일합니다.");
        }

        // 다른 PUBLIC 채널에서 동일한 이름을 사용하는지 확인
        boolean duplicated = data.stream()
                .anyMatch(otherChannel -> otherChannel.getType() == ChannelType.PUBLIC
                        && otherChannel != targetChannel
                        && otherChannel.getName().equals(request.channelName()));

        // 중복된 채널 이름이 있으면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 채널 이름입니다.");
        }

        targetChannel.changeName(request.channelName()); // 이름을 변경하고 수정 시각을 갱신한다.
        return toDto(targetChannel); // 수정한 채널을 DTO로 변환하여 반환한다.
    }

    // 채널 및 관련 데이터를 삭제
    @Override
    public void deleteChannel(UUID id) {
        Channel targetChannel = findChannel(id);

        // 채널에 연결된 메시지와 읽음 상태를 삭제한 후 채널 삭제
        messageRepository.deleteByChannelId(id);
        readStatusRepository.deleteByChannelId(id);
        data.remove(targetChannel);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findChannel(channelId);

        // 참여하려는 유저의 존재 여부 확인
        if (userRepository.findByUserId(userId).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // PRIVATE 채널은 생성 당시 지정된 참여자만 입장 허용
        if (channel.getType() == ChannelType.PRIVATE
                && !channel.hasMember(userId)) {
            throw new IllegalArgumentException("PRIVATE 채널의 참여자가 아닙니다.");
        }

        // 아직 참여하지 않은 유저라면 채널에 추가
        if (!channel.hasMember(userId)) {
            channel.addMember(userId);
        }

        // 기존 ReadStatus가 없으면 새로 생성하여 저장
        boolean exists = readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .isPresent();

        if (!exists) {
            readStatusRepository.save(
                    new ReadStatus(userId, channelId, Instant.now())
            );
        }
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findChannel(channelId);

        // 채널에서 유저를 제외하고 해당 유저의 읽음 상태 삭제
        channel.exitMember(userId);

        readStatusRepository.deleteByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public boolean isMember(UUID channelId, UUID userId) {
        return findChannel(channelId).hasMember(userId);
    }

    // 전달받은 ID와 일치하는 실제 Channel 객체를 찾아 반환한다.
    private Channel findChannel(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));
    }

    // 해당 채널의 가장 최근 메시지 시간 조회
    private Instant findLastMessageAt(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    // Channel 객체를 ChannelDto로 변환한다.
    private ChannelDto toDto(Channel channel) {
        Set<UUID> memberIds = channel.getMemberIds();
//        Set<UUID> memberIds = channel.getType() == ChannelType.PRIVATE
//                ? channel.getMemberIds() : Set.of();

        return new ChannelDto(
                channel.getId(), channel.getType(),
                channel.getName(), channel.getDescription(),
                memberIds, channel.getCreatedAt(),
                channel.getUpdatedAt(), findLastMessageAt(channel.getId())
        );
    }
}