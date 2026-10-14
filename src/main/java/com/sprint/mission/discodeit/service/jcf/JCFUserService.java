package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        this.data.put(user.getId(), user);
        return user;
    }

    public User updateName(UUID id, String name) {
        User user = this.findById(id);
        if (user == null) throw new NoSuchElementException("수정할 유저가 존재하지 않습니다.");
        return user.updateName(name);
    }

    public User updateChannels(UUID id, Map<UUID, Channel> channels) {
        User user = this.findById(id);
        if (user == null) throw new NoSuchElementException("수정할 유저가 존재하지 않습니다.");
        return user.updateChannels(channels);
    }

    public void addChannelToUser(UUID userId, Channel channel) {
        User user = this.findById(userId);
        if (user == null) throw new IllegalArgumentException("채널을 추가할 유저가 존재하지 않습니다.");

        Map<UUID, Channel> channels = user.getChannels();
        channels.put(channel.getId(), channel);
        user.updateChannels(channels);

        // 유저에 채널 추가 시 채널에도 유저 추가
        channel.addUser(user);
    }

    public void removeChannelFromUser(UUID userId, UUID channelId) {
        User user = this.findById(userId);
        if (user == null) throw new IllegalArgumentException("채널을 삭제할 유저가 존재하지 않습니다.");

        Map<UUID, Channel> channels = user.getChannels();

        // 유저에 채널 삭제 시 채널에도 유저 삭제
        channels.values()
                .stream()
                .filter(channel -> channel.getId() == channelId)
                .findFirst()
                .ifPresent(channel -> channel.removeUser(userId));

        channels.remove(channelId);
        user.updateChannels(channels);
    }

    @Override
    public User findById(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        User user = this.findById(id);
        if (user == null) throw new IllegalArgumentException("삭제할 유저가 존재하지 않습니다.");

        // 유저 삭제 시 채널에도 유저 삭제
        Map<UUID, Channel> channels = user.getChannels();
        channels.values().forEach(channel -> channel.removeUser(user.getId()));

        this.data.remove(id);
    }
}
