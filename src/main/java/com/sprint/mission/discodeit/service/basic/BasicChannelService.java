package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelMapper channelMapper;

  @Override
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
    Set<UUID> memberIds = new LinkedHashSet<>(request.participantIds());

    List<User> members =
        memberIds.stream()
            .map(id ->
                userRepository
                    .findById(id)
                    .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다.")))
            .toList();

    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    memberIds.forEach(channel::addUser);
    channelRepository.save(channel);

    for (User member : members) {
      member.addChannel(channel.getId());
      userRepository.save(member);
      readStatusRepository.save(new ReadStatus(member.getId(), channel.getId(), Instant.now()));
    }

    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto createPublic(PublicChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    channelRepository.save(channel);

    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto findById(UUID id) {
    Channel channel = getChannelOrThrow(id);
    return channelMapper.toDto(channel);
  }

  private Channel getChannelOrThrow(UUID id) {
    return channelRepository
        .findById(id)
        .orElseThrow(() -> new ChannelNotFoundException("존재하지 않는 채널입니다."));
  }

  @Override
  public List<ChannelDto> findAll() {
    return channelRepository.findAll().stream().map(channelMapper::toDto).toList();
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel -> channel.getType() == ChannelType.PUBLIC || channel.hasUser(userId))
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  public ChannelDto update(UUID id, ChannelUpdateRequest request) {
    Channel channel = getChannelOrThrow(id);
    if (channel.getType() == ChannelType.PRIVATE)
      throw new PrivateChannelUpdateNotAllowedException("PRIVATE 채널은 수정할 수 없습니다.");

    channel.update(request.newName(), request.newDescription());
    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  @Override
  public void delete(UUID id) {
    Channel channel = getChannelOrThrow(id);

    List<Message> messages = messageRepository.findAllByChannelId(id);
    for (Message message : messages) {
      message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
    }
    messageRepository.deleteAllByChannelId(id);

    readStatusRepository.deleteAllByChannelId(id);

    Map<UUID, List<UUID>> messageIdsByAuthor =
        messages.stream()
            .collect(
                Collectors.groupingBy(
                    Message::getAuthorId, Collectors.mapping(Message::getId, Collectors.toList())));

    Set<UUID> affectedUserIds = new HashSet<>(channel.getUsers());
    affectedUserIds.addAll(messageIdsByAuthor.keySet());

    for (UUID userId : affectedUserIds) {
      userRepository
          .findById(userId)
          .ifPresent(user -> {
            if (user.hasChannel(id)) {
              user.removeChannel(id);
            }
            messageIdsByAuthor.getOrDefault(userId, List.of()).forEach(user::removeMessage);
            userRepository.save(user);
          });
    }

    channelRepository.deleteById(id);
  }

  @Override
  public void join(UUID id, UUID userId) {
    Channel channel = getChannelOrThrow(id);
    if (channel.getType() == ChannelType.PRIVATE)
      throw new PrivateChannelJoinNotAllowedException("PRIVATE 채널에는 참여할 수 없습니다.");

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

    channel.addUser(userId);
    user.addChannel(id);
    channelRepository.save(channel);
    userRepository.save(user);
  }

  @Override
  public void leave(UUID id, UUID userId) {
    Channel channel = getChannelOrThrow(id);

    channel.removeUser(userId);
    channelRepository.save(channel);
    userRepository
        .findById(userId)
        .ifPresent(user -> {
          user.removeChannel(id);
          userRepository.save(user);
        });
    readStatusRepository
        .findByUserIdAndChannelId(userId, id)
        .ifPresent(rs -> readStatusRepository.deleteById(rs.getId()));
  }
}
