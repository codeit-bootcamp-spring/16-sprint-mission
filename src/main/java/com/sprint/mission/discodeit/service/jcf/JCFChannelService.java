package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private UserService userService;

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
    public Channel update(UUID id, Channel newChannel) {
        Channel channel = this.findById(id).orElseThrow(() -> new NoSuchElementException("수정할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();
        channel.update(newChannel);
        this.data.put(id, channel);
        return channel;
    }

    @Override
    public void addUserToChannel(UUID channelId, UUID userId) {
        // 유저가 존재하는지 확인
        User user = this.userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("채널에 추가할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Channel channel = this.findById(channelId).orElseThrow(
                () -> new NoSuchElementException("유저를 추가할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        channel.addUser(user.getId());
        this.update(channelId, channel);

        // 채널에 유저 추가 시 유저에도 채널 추가
        user.addChannel(channel.getId());
        this.userService.update(userId, user);
    }

    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        // 유저가 존재하는지 확인
        User user = this.userService.findById(userId).orElseThrow(
                () -> new NoSuchElementException("채널에서 탈퇴시킬 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Channel channel = this.findById(channelId).orElseThrow(
                () -> new NoSuchElementException("유저가 탈퇴할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        // 채널의 유저 탈퇴 시 유저에도 채널 지우기
        user.removeChannel(channelId);
        this.userService.update(userId, user);

        channel.removeUser(userId);
        this.update(channelId, channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = this.findById(id).orElseThrow(
                () -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        // 채널 삭제 시 유저에도 채널 삭제
        channel.getUsers().forEach(userId -> this.userService.findById(userId).ifPresent(user -> {
            user.removeChannel(id);
            this.userService.update(userId, user);
        }));
        channel.softDelete();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
