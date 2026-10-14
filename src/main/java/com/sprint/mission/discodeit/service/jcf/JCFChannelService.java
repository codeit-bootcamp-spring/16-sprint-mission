package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
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

    public Channel updateName(UUID id, String name) {
        Channel channel = this.findById(id);
        if (channel == null) throw new NoSuchElementException("수정할 채널이 존재하지 않습니다.");
        return channel.updateName(name);
    }

    public Channel updateUsers(UUID id, Map<UUID, User> users) {
        Channel channel = this.findById(id);
        if (channel == null) throw new NoSuchElementException("수정할 채널이 존재하지 않습니다.");
        return channel.updateUsers(users);
    }

    public Channel updateMessages(UUID id, Map<UUID, Message> messages) {
        Channel channel = this.findById(id);
        if (channel == null) throw new NoSuchElementException("수정할 채널이 존재하지 않습니다.");
        return channel.updateMessages(messages);
    }

    public void addUserToChannel(UUID channelId, User user) {
        Channel channel = this.findById(channelId);
        if (channel == null) throw new NoSuchElementException("유저를 추가할 채널이 존재하지 않습니다.");

        Map<UUID, User> users = channel.getUsers();
        users.put(user.getId(), user);
        channel.updateUsers(users);

        // 채널에 유저 추가 시 유저에도 채널 추가
        user.addChannel(channel);
    }

    public void removeUserFromChannel(UUID channelId, UUID userId) {
        Channel channel = this.findById(channelId);
        if (channel == null) throw new NoSuchElementException("유저를 삭제할 채널이 존재하지 않습니다.");

        Map<UUID, User> users = channel.getUsers();
        
        // 채널에 유저 삭제 시 유저에도 채널 삭제
        users.values()
                .stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .ifPresent(user -> user.removeChannel(channelId));

        users.remove(userId);
        channel.updateUsers(users);
    }

    @Override
    public Channel findById(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = this.findById(id);
        if (channel == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");

        // 채널 삭제 시 유저에도 채널 삭제
        Map<UUID, User> users = channel.getUsers();
        users.values().forEach(user -> user.removeChannel(id));

        this.data.remove(id);
    }
}
