package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        this.data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        return channel.update(name);
    }

    @Override
    public void addUser(UUID id, User user) {
        Channel channel = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        Map<UUID, User> users = channel.getUsers();
        users.put(user.getId(), user);
        channel.update(users);

        // 채널에 유저 추가 시 유저에도 채널 추가
        Map<UUID, Channel> channels = user.getChannels();
        channels.put(id, channel);
        user.update(channels);
    }

    @Override
    public void removeUser(UUID channelId, UUID userId) {
        Channel channel = this.findById(channelId).orElseThrow(() -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        User removedUser = channel.getUsers().remove(userId);
        if (removedUser == null) throw new NoSuchElementException("삭제할 유저가 존재하지 않습니다.");

        // 채널에 유저 제거 시 유저에도 체널 제거
        Channel removedChannel = removedUser.getChannels().remove(channelId);
        if (removedChannel == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<List<Channel>> findAll() {
        return Optional.of(this.data.values().stream().toList());
    }

    @Override
    public void delete(UUID id) {
        this.findById(id).orElseThrow(() ->
                new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        Channel removedChannel = this.data.remove(id);

        // 채널 삭제 시 모든 등록 유저들에서 채널 삭제
        removedChannel.getUsers().values().forEach(user -> {
            Map<UUID, Channel> channels = user.getChannels();
            Channel removed = channels.remove(id);
            if (removed == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");
            user.update(channels);
        });
    }
}
