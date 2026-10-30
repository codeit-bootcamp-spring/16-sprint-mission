package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    public FileChannelService() {
        this.channelRepository = new FileChannelRepository();
        this.userRepository = new FileUserRepository();
        this.messageRepository = new FileMessageRepository();
        this.readStatusRepository = new FileReadStatusRepository();
        this.binaryContentRepository = new FileBinaryContentRepository();
    }

    @Override
    public ChannelDto createPublic(ChannelCreateRequest request) {
        Objects.requireNonNull(request, "채널 생성 요청은 필수입니다.");

        boolean duplicate = channelRepository.findAll().stream()
                .anyMatch(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                && Objects.equals(channel.getName(), request.name())
                );

        if (duplicate) {
            throw new IllegalArgumentException("이미 사용 중인 채널 이름입니다.");
        }

        Channel channel = new Channel(
                request.name(),
                request.description(),
                ChannelType.PUBLIC
        );

        return toDto(channelRepository.save(channel));
    }

    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
        Objects.requireNonNull(request, "비공개 채널 생성 요청은 필수입니다.");

        List<UUID> participantIds = request.participantIds();

        if (participantIds == null || participantIds.isEmpty()) {
            throw new IllegalArgumentException("비공개 채널에는 참여자가 필요합니다.");
        }

        if (participantIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("참여자 ID는 null일 수 없습니다.");
        }

        if (participantIds.size() != participantIds.stream().distinct().count()) {
            throw new IllegalArgumentException("중복된 참여자가 있습니다.");
        }

        for (UUID participantId : participantIds) {
            userRepository.findById(participantId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("존재하지 않는 사용자입니다.")
                    );
        }

        Channel channel = new Channel(null, null, ChannelType.PRIVATE);
        Channel saved = channelRepository.save(channel);

        for (UUID participantId : participantIds) {
            readStatusRepository.save(
                    new ReadStatus(participantId, saved.getId())
            );
        }

        return toDto(saved);
    }

    @Override
    public Optional<ChannelDto> findById(UUID id) {
        Objects.requireNonNull(id, "채널 ID는 필수입니다.");

        return channelRepository.findById(id)
                .map(this::toDto);
    }

    @Override
    public List<ChannelDto> findAll() {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || readStatusRepository
                                .findByUserIdAndChannelId(userId, channel.getId())
                                .isPresent()
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChannelDto update(ChannelUpdateRequest request) {
        Objects.requireNonNull(request, "채널 수정 요청은 필수입니다.");

        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다.")
                );

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        boolean duplicate = channelRepository.findAll().stream()
                .anyMatch(other ->
                        !other.getId().equals(channel.getId())
                                && other.getType() == ChannelType.PUBLIC
                                && Objects.equals(other.getName(), request.name())
                );

        if (duplicate) {
            throw new IllegalArgumentException("이미 사용 중인 채널 이름입니다.");
        }

        channel.update(request.name(), request.description());

        return toDto(channelRepository.save(channel));
    }

    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "채널 ID는 필수입니다.");

        channelRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다.")
                );

        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> id.equals(message.getChannelId()))
                .toList();

        for (Message message : messages) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }

            messageRepository.deleteById(message.getId());
        }

        List<ReadStatus> statuses = readStatusRepository.findAll().stream()
                .filter(status -> id.equals(status.getChannelId()))
                .toList();

        for (ReadStatus status : statuses) {
            readStatusRepository.deleteById(status.getId());
        }

        channelRepository.deleteById(id);
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        Objects.requireNonNull(channelId, "채널 ID는 필수입니다.");
        Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다.")
                );

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

        if (channel.getType() == ChannelType.PUBLIC) {
            return;
        }

        if (readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .isPresent()) {
            throw new IllegalArgumentException("이미 참여 중인 채널입니다.");
        }

        readStatusRepository.save(new ReadStatus(userId, channelId));
    }

    @Override
    public void leave(UUID channelId, UUID userId) {
        Objects.requireNonNull(channelId, "채널 ID는 필수입니다.");
        Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다.")
                );

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

        if (channel.getType() == ChannelType.PUBLIC) {
            return;
        }

        ReadStatus status = readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("참여 중인 채널이 아닙니다.")
                );

        readStatusRepository.deleteById(status.getId());
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(message ->
                        channel.getId().equals(message.getChannelId())
                )
                .map(Message::getCreatedAt)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds =
                channel.getType() == ChannelType.PRIVATE
                        ? readStatusRepository.findAll().stream()
                        .filter(status ->
                                channel.getId().equals(status.getChannelId())
                        )
                        .map(ReadStatus::getUserId)
                        .toList()
                        : List.of();

        return new ChannelDto(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType(),
                lastMessageAt,
                participantIds
        );
    }
}