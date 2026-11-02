package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.Channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelPublicCreateResponse createPublic(ChannelPublicCreateRequest createRequest) {
        String name = createRequest.name();
        String description = createRequest.description();

        Channel channel = new Channel(name, description, ChannelType.PUBLIC);
        this.channelRepository.save(channel);

        return ChannelPublicCreateResponse.from(channel);
    }

    @Override
    public ChannelPrivateCreateResponse createPrivate(ChannelPrivateCreateRequest createRequest) {
        List<UUID> memberIds = createRequest.memberIds();
        Channel channel = new Channel(memberIds, ChannelType.PRIVATE);
        List<UUID> distinctMemberIds = new ArrayList<>(new LinkedHashSet<>(memberIds));

        distinctMemberIds.forEach(memberId -> {
            this.userRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("생성할 비공개 채널에 소속될 유저가 존재하지 않습니다."));
            if (this.readStatusRepository.findByChannelIdAndUserId(channel.getId(), memberId).isPresent()) {
                throw new IllegalStateException("이미 존재하는 메세지 확인 기록입니다.");
            }
        });

        this.channelRepository.save(channel);

        distinctMemberIds.forEach(memberId -> {
            this.readStatusRepository.save(new ReadStatus(memberId, channel.getId()));
        });

        return ChannelPrivateCreateResponse.from(channel);
    }

    @Override
    public ChannelFindResponse findById(UUID id) {
        Channel channel = this.channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        Instant lastMessageTime = this.messageRepository.findLastMessageOfChannel(id)
                .map(BaseEntity::getCreatedAt)
                .orElse(null);
        return ChannelFindResponse.of(channel, lastMessageTime);
    }

    @Override
    public List<ChannelFindResponse> findAllByUserId(UUID userId) {
        return this.channelRepository.findAll()
                .stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || (channel.getType() == ChannelType.PRIVATE && channel.getUsers().contains(userId)))
                .map(channel -> {
                    Instant lastMessageTime = this.messageRepository.findLastMessageOfChannel(channel.getId())
                            .map(BaseEntity::getCreatedAt)
                            .orElse(null);
                    return ChannelFindResponse.of(channel, lastMessageTime);
                })
                .toList();
    }

    @Override
    public ChannelUpdateResponse update(ChannelUpdateRequest updateRequest) {
        UUID id = updateRequest.id();

        Channel channel = this.channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();
        if (channel.getType() == ChannelType.PRIVATE) throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");

        String newName = updateRequest.name();
        String newDescription = updateRequest.description();

        channel.update(newName, newDescription);
        this.channelRepository.update(id, channel);
        return ChannelUpdateResponse.from(channel);
    }

    @Override
    public void addUserToChannel(UUID channelId, UUID userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("채널에 추가할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("유저를 추가할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        channel.addUser(user.getId());
        this.channelRepository.update(channelId, channel);
    }

    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        // 유저가 존재하는지 확인
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("채널에서 탈퇴시킬 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Channel channel = this.channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("유저가 탈퇴할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        channel.removeUser(userId);
        this.channelRepository.update(channelId, channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = this.channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        // 채널 -> 메세지 -> 첨부 파일 삭제
        this.messageRepository.findAllByChannelId(id).forEach(message -> {
            List<UUID> distinctAttachmentIds = new ArrayList<>(new LinkedHashSet<>(message.getAttachmentIds()));

            this.binaryContentRepository.findAllByIdIn(distinctAttachmentIds).forEach(binaryContent -> {
                this.binaryContentRepository.delete(binaryContent.getId());
            });
            this.messageRepository.delete(message.getId());
        });

        this.readStatusRepository.findAllByChannelId(id).forEach(readStatus -> {
            this.readStatusRepository.delete(readStatus.getId());
        });

        this.channelRepository.delete(id);
    }
}
