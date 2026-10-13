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

    @Override
    public User update(UUID id, String name) {
        User user = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        return user.update(name);
    }

    @Override
    public void addChannel(UUID id, Channel channel) {
        User user = this.findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        Map<UUID, Channel> channels = user.getChannels();
        channels.put(channel.getId(), channel);
        user.update(channels);

        // 유저에 채널 추가 시 채널에도 유저 추가
        Map<UUID, User> users = channel.getUsers();
        users.put(id, user);
        channel.update(users);
    }

    @Override
    public void removeChannel(UUID userId, UUID channelId) {
        User user = this.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        Channel removedChannel = user.getChannels().remove(channelId);
        if (removedChannel == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");

        // 유저에 채널 제거 시 채널에도 유저 제거
        User removedUser = removedChannel.getUsers().remove(userId);
        if (removedUser == null) throw new NoSuchElementException("삭제할 유저가 존재하지 않습니다.");
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<List<User>> findAll() {
        return Optional.of(this.data.values().stream().toList());
    }

    @Override
    public void delete(UUID id) {
        this.findById(id).orElseThrow(() -> new IllegalArgumentException("삭제할 유저가 존재하지 않습니다."));
        User removedUser = this.data.remove(id);

        // 유저 삭제 시 모든 등록 채널에서 유저 삭제
        removedUser.getChannels().values().stream().forEach(channel -> {
            Map<UUID, User> users = channel.getUsers();
            User removed = users.remove(id);
            if (removed == null) throw new NoSuchElementException("삭제할 유저가 존재하지 않습니다.");
            channel.update(users);
        });
    }
}
