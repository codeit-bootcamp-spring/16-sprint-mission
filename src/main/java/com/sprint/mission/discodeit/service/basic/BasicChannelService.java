package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.exceptions.DuplicateResourceException;
import com.sprint.mission.discodeit.exceptions.ForbiddenOperationException;
import com.sprint.mission.discodeit.exceptions.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.type.ChannelType;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.*;

@Service // 해당 클래스를 Spring의 Service 계층 Bean으로 등록하여, Spring IoC 컨테이너가 관리하도록 한다.
@RequiredArgsConstructor // final 필드와 @NonNull 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 생성하도록 한다.
public class BasicChannelService implements ChannelService {

    // 채널 및 관련 데이터 관리를 담당하는 Repository
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {

        // 1. PUBLIC 채널 이름 중복 검사
        boolean duplicated = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .anyMatch(channel -> channel.getName().equals(request.channelName()));

        if (duplicated) {
            throw new DuplicateResourceException("중복된 채널 이름입니다.");
        }

        // 2. PUBLIC 채널 생성 및 저장
        Channel channel = new Channel(request.channelName(), request.description());
        channelRepository.save(channel);

        return toDto(channel);
    }

    // PRIVATE 채널 생성
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        List<UUID> memberIds = request.memberIds();

        // 1. 참여자 목록 유효성 검사
        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("참여자가 필요합니다.");
        }

        // 2. 모든 참여자의 존재 여부 확인
        for (UUID userId : memberIds) {
            userRepository.findByUserId(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("유저를 찾을 수 없습니다."));
        }

        // 3. PRIVATE 채널 생성 및 저장
        Channel channel = new Channel(memberIds);
        channelRepository.save(channel);

        return toDto(channel);
    }

    @Override
    public ChannelDto readChannel(UUID id) {
        // 채널을 조회하여 DTO로 변환한다.
        return toDto(findChannel(id));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {

        // 사용자가 존재하는지 확인한다.
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("사용자를 찾을 수 없습니다.")
                );

        // 모든 PUBLIC 채널과 해당 사용자가 참여한 PRIVATE 채널을 조회한다.
        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC || channel.hasMember(userId))
                .map(this::toDto)
                .toList();
    }

    // 해당 채널의 가장 최근 메시지 생성 시각을 조회한다.
    private Instant findLastMessageAt(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public ChannelDto updateChannel(UUID channelId, PublicChannelUpdateRequest request) {

        // 1. 수정 대상 채널 조회
        Channel targetChannel = channelRepository.findByChannelId(channelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("채널을 찾을 수 없습니다."));

        // 2. PRIVATE 채널 수정을 제한한다.
        if (targetChannel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // 3. 수정할 채널 이름과 설명을 결정
        String newChannelName = request.channelName() != null
                ? request.channelName() : targetChannel.getName();

        String newDescription = request.description() != null
                ? request.description() : targetChannel.getDescription();

        // 4. 다른 PUBLIC 채널과 이름이 중복되는지 확인한다.
        boolean duplicated = channelRepository.findAll().stream()
                .filter(otherChannel -> otherChannel.getType() == ChannelType.PUBLIC)
                .anyMatch(otherChannel ->
                        !otherChannel.getId().equals(channelId)
                                && otherChannel.getName().equals(newChannelName));

        // 5. 중복된 채널 이름이 있으면 예외를 발생시킨다.
        if (duplicated) {
            throw new DuplicateResourceException("중복된 채널 이름입니다.");
        }

        // 6. 변경 사항이 있는지 확인한다.
        if (Objects.equals(targetChannel.getName(), newChannelName)
                && Objects.equals(targetChannel.getDescription(), newDescription)) {
            throw new IllegalArgumentException("기존 채널 이름과 내용에는 변경사항이 없습니다.");
        }

        // 7. 채널 이름과 설명을 변경하고 수정 정보를 저장한다.
        targetChannel.changeName(newChannelName);
        targetChannel.changeDescription(newDescription);

        channelRepository.save(targetChannel);

        return toDto(targetChannel);
    }

    @Override
    public void deleteChannel(UUID id) {

        // 1. 삭제 대상 채널 존재 여부 확인
        findChannel(id);

        // 2. 해당 채널의 모든 메시지 조회
        List<Message> messages = messageRepository.findAllByChannelId(id);

        // 3. 메시지에 연결된 첨부파일 ID 수집
        List<UUID> attachmentIds = messages.stream()
                .filter(message -> message.getAttachmentIds() != null)
                .flatMap(message -> message.getAttachmentIds().stream())
                .distinct()
                .toList();

        // 4. 첨부파일 삭제
        attachmentIds.forEach(binaryContentRepository::deleteById);

        // 5. 채널과 연관된 메시지 및 읽음 상태 삭제
        messageRepository.deleteByChannelId(id);
        readStatusRepository.deleteByChannelId(id);

        // 6. 채널 삭제
        channelRepository.deleteByChannelId(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {

        // 1. 참여하려는 사용자의 존재 여부 확인
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        // 2. 참여 대상 채널 조회
        Channel channel = findChannel(channelId);

        // 3. PRIVATE 채널은 기존 참여자만 접근이 가능
        if (channel.getType() == ChannelType.PRIVATE
                && !channel.hasMember(userId)) {
            throw new ForbiddenOperationException("PRIVATE 채널에 참여할 수 없습니다.");
        }

        // 이미 참여 중인 사용자는 별도 처리하지 않는다.
        if (channel.hasMember(userId)) {
            return;
        }

        // 4. 채널 참여 정보 갱신 및 저장
        channel.addMember(userId);
        channelRepository.save(channel);

        // 5. 해당 사용자의 ReadStatus가 없는 경우 생성
        if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isEmpty()) {
            readStatusRepository.save(
                    new ReadStatus(userId, channelId, Instant.now()));
        }
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {

        // 1. 탈퇴하려는 사용자의 존재 여부 확인
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        // 2. 탈퇴 대상 채널 조회
        Channel channel = findChannel(channelId);

        // 3. 채널 참여 정보 갱신 및 저장
        channel.exitMember(userId);
        channelRepository.save(channel);

        // 4. 해당 사용자의 채널 읽음 상태 삭제
        readStatusRepository.deleteByUserIdAndChannelId(userId, channelId);
    }

    @Override
    public boolean isMember(UUID channelId, UUID userId) {
        // 해당 채널에 사용자가 참여 중인지 확인한다.
        return findChannel(channelId).hasMember(userId);
    }

    // ID에 해당하는 Channel 객체를 조회한다.
    private Channel findChannel(UUID id) {
        return channelRepository.findByChannelId(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("채널을 찾을 수 없습니다."));
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