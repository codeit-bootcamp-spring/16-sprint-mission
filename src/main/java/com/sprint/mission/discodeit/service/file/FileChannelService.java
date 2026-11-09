package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.exceptions.DuplicateResourceException;
import com.sprint.mission.discodeit.exceptions.ForbiddenOperationException;
import com.sprint.mission.discodeit.type.ChannelType;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.newDomain.ReadStatus;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.otherRepository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    // Repository 의존성
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    // 생성자 의존성 주입
    public FileChannelService(ChannelRepository channelRepository,
                              UserRepository userRepository,
                              MessageRepository messageRepository,
                              ReadStatusRepository readStatusRepository) {

        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
    }

    // PUBLIC 채널 생성
    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {

        // PUBLIC 채널 이름 중복 검사
        boolean duplicated = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .anyMatch(channel -> channel.getName().equals(request.channelName()));

        if (duplicated) {
            throw new DuplicateResourceException("중복된 채널 이름입니다.");
        }

        // PUBLIC 채널 생성 및 저장
        Channel channel = new Channel(request.channelName(), request.description());
        channelRepository.save(channel);

        return toDto(channel);
    }

    // PRIVATE 채널 생성
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {

        List<UUID> memberIds = request.memberIds();

        // 참여자 목록 유효성 검사
        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("참여자가 필요합니다.");
        }

        // 모든 참여자의 존재 여부 확인
        for (UUID userId : memberIds) {
            userRepository.findByUserId(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("유저를 찾을 수 없습니다.")
                    );
        }

        // PRIVATE 채널 생성 및 저장
        Channel channel = new Channel(memberIds);
        channelRepository.save(channel);

        // 참여자별 읽음 상태 생성 및 저장
        for (UUID userId : channel.getMemberIds()) {
            ReadStatus readStatus =
                    new ReadStatus(userId, channel.getId(), Instant.now());

            readStatusRepository.save(readStatus);
        }

        return toDto(channel);
    }

    @Override
    public ChannelDto readChannel(UUID id) {

        // ID에 해당하는 채널 조회
        return toDto(findChannel(id));
    }

    @Override
    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {

        // 사용자가 존재하는지 확인한다.
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("사용자를 찾을 수 없습니다.")
                );

        // PUBLIC 채널과 해당 사용자가 참여한 PRIVATE 채널 조회
        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || channel.hasMember(userId))
                .map(this::toDto)
                .toList();
    }

    // 채널의 가장 최근 메시지 작성 시간 조회
    private Instant findLastMessageAt(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public ChannelDto updateChannel(ChannelUpdateRequest request) {

        // 수정 대상 채널 조회
        Channel channel = findChannel(request.channelId());

        // PRIVATE 채널 수정 제한
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new ForbiddenOperationException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // 기존 채널 이름과 변경할 이름이 동일한지 확인
        if (channel.getName().equals(request.channelName())) {
            throw new IllegalArgumentException("기존 채널명과 동일합니다.");
        }

        // 다른 PUBLIC 채널의 이름과 중복되는지 확인
        boolean duplicated = channelRepository.findAll().stream()
                .filter(otherChannel -> otherChannel.getType() == ChannelType.PUBLIC)
                .anyMatch(otherChannel ->
                        !otherChannel.getId().equals(request.channelId())
                            && otherChannel.getName().equals(request.channelName()));

        // 중복된 채널 이름이 있으면 예외를 발생시킨다.
        if (duplicated) {
            throw new DuplicateResourceException("중복된 채널 이름입니다.");
        }

        // 채널 이름 변경 및 저장
        channel.changeName(request.channelName());
        channelRepository.save(channel);

        return toDto(channel);
    }

    // 전달받은 ID와 일치하는 채널을 찾아 삭제한다.
    @Override
    public void deleteChannel(UUID id) {

        // 삭제 대상 채널 존재 여부 확인
        findChannel(id);

        // 채널과 연관된 데이터를 먼저 삭제한다.
        messageRepository.deleteByChannelId(id);
        readStatusRepository.deleteByChannelId(id);

        // 채널 삭제
        channelRepository.deleteByChannelId(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {

        // 참여 요청자의 존재 여부를 확인한다.
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        Channel channel = findChannel(channelId);

        // PRIVATE 채널은 기존 참여자만 이용 가능하다.
        if (channel.getType() == ChannelType.PRIVATE
                && !channel.hasMember(userId)){
            throw new ForbiddenOperationException("PRIVATE 채널에 참여할 수 없습니다.");
        }

        // 이미 참여 중인 사용자는 추가 작업 없이 종료
        if (channel.hasMember(userId)){
            return;
        }

        // 채널에 사용자 추가 및 변경 사항 저장
        channel.addMember(userId);
        channelRepository.save(channel);

        // 기존 읽음 상태가 없는 경우 생성 및 저장
        if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isEmpty()){
            readStatusRepository.save(
                    new ReadStatus(userId, channelId, Instant.now()));
        }
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {

        // 퇴장 요청자의 존재 여부 확인
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저를 찾을 수 없습니다.")
                );

        Channel channel = findChannel(channelId);

        // 채널에서 사용자 제거 및 변경 사항 저장
        channel.exitMember(userId);
        channelRepository.save(channel);

        // 해당 사용자의 채널 읽음 상태 삭제
        readStatusRepository.deleteByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public boolean isMember(UUID channelId, UUID userId) {

        // 채널을 조회하고 해당 유저가 멤버인지 확인한다.
        return findChannel(channelId).hasMember(userId);
    }

    // ID와 일치하는 실제 Channel 객체를 반환한다.
    private Channel findChannel(UUID id) {
        return channelRepository.findByChannelId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("채널을 찾을 수 없습니다."));
    }

    // Channel 객체를 ChannelDto로 변환한다.
    private ChannelDto toDto(Channel channel) {
        Set<UUID> memberIds = channel.getType() == ChannelType.PRIVATE
                ? channel.getMemberIds() : Set.of();

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                memberIds,
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                findLastMessageAt(channel.getId())
        );
    }
}