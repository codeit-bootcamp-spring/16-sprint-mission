package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.FileObjectStore;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final FileObjectStore<Channel> store;
    private final UserService userService;

    public FileChannelService(UserService userService, Path directory) {
        this.store = new FileObjectStore<>(directory.resolve("channels"), Channel.class);
        this.userService = userService;
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
        return store.save(new Channel(type, name.trim(), description));
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = store.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return store.findAll();
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = findById(id);
        validateChannel(name, description, id);
        channel.update(name.trim(), description);
        return store.save(channel);
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

        store.delete(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        if (channel.getMemberIds().contains(userId)) {
            throw new IllegalArgumentException("이미 참여 중인 사용자입니다.");
        }

        channel.addMember(userId);
        user.addChannel(channelId);

        store.save(channel);
        userService.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

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

        store.save(channel);
        userService.save(user);
    }

    private void validateChannel(String name, String description, UUID excludeId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름을 입력해야 합니다.");
        }

        if (description == null) {
            throw new IllegalArgumentException("채널 설명은 null일 수 없습니다.");
        }

        for (Channel channel : store.findAll()) {
            if (!channel.getId().equals(excludeId)
                    && channel.getName().equalsIgnoreCase(name.trim())) {
                throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
            }
        }
    }
}