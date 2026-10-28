package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository,
                               UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel create(String name, String description) {
        return create(ChannelType.PUBLIC, name, description);
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        if (type == null) {
            throw new IllegalArgumentException("채널 타입이 없습니다.");
        }

        validateChannel(name, description, null);
        return channelRepository.save(new Channel(type, name.trim(), description));
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = findById(id);
        validateChannel(name, description, id);
        channel.update(name.trim(), description);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);

        if (!channel.getMessageIds().isEmpty()) {
            throw new IllegalStateException("메시지가 남아 있어 채널을 삭제할 수 없습니다.");
        }

        if (!channel.getMemberIds().isEmpty()) {
            throw new IllegalStateException("참여 중인 사용자가 있어 채널을 삭제할 수 없습니다.");
        }

        channelRepository.delete(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = findUser(userId);

        if (channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("이미 참여 중인 사용자입니다.");
        }

        channel.addMember(userId);
        user.addChannel(channelId);
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = findUser(userId);

        if (!channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("참여하지 않은 사용자입니다.");
        }

        for (UUID messageId : user.getMessageIds()) {
            if (channel.getMessageIds().contains(messageId)) {
                throw new IllegalStateException("채널에 작성한 메시지가 남아 있어 탈퇴할 수 없습니다.");
            }
        }

        channel.removeMember(userId);
        user.removeChannel(channelId);
        channelRepository.save(channel);
        userRepository.save(user);
    }

    private User findUser(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        return user;
    }

    private void validateChannel(String name, String description, UUID excludeId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름을 입력해야 합니다.");
        }

        if (description == null) {
            throw new IllegalArgumentException("채널 설명은 null일 수 없습니다.");
        }

        for (Channel channel : channelRepository.findAll()) {
            if (!channel.getId().equals(excludeId)
                    && channel.getName().equalsIgnoreCase(name.trim())) {
                throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
            }
        }
    }
}