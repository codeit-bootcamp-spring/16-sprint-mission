package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

public class UserChannelManager {
    private final UserService userService;
    private final ChannelService channelService;

    public UserChannelManager(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    public void addUserToChannel(UUID channelId, UUID userId) {
        // 유저가 존재하는지 확인
        User user = this.userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("채널에 추가할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Channel channel = this.channelService.findById(channelId).orElseThrow(
                () -> new NoSuchElementException("유저를 추가할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        channel.addUser(user.getId());
        this.channelService.update(channelId, channel);

        // 채널에 유저 추가 시 유저에도 채널 추가
        user.addChannel(channel.getId());
        this.userService.update(userId, user);
    }

    public void removeUserFromChannel(UUID channelId, UUID userId) {
        // 유저가 존재하는지 확인
        User user = this.userService.findById(userId).orElseThrow(
                () -> new NoSuchElementException("채널에서 탈퇴시킬 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Channel channel = this.channelService.findById(channelId).orElseThrow(
                () -> new NoSuchElementException("유저가 탈퇴할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        // 채널의 유저 탈퇴 시 유저에도 채널 지우기
        user.removeChannel(channelId);
        this.userService.update(userId, user);

        channel.removeUser(userId);
        this.channelService.update(channelId, channel);
    }

    public void deleteChannel(UUID channelId) {
        Channel channel = this.channelService.findById(channelId).orElseThrow(
                () -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        // 채널 삭제 시 유저에도 채널 삭제
        channel.getUsers().forEach(userId -> this.userService.findById(userId).ifPresent(user -> {
            user.removeChannel(channelId);
            this.userService.update(userId, user);
        }));
        channel.softDelete();
    }

    public void deleteUser(UUID userId) {
        User user = this.userService.findById(userId).orElseThrow(
                () -> new NoSuchElementException("삭제할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        // 유저 삭제 시 가입된 채널들에서도 유저 탈퇴
        Set<UUID> channels = new HashSet<>(user.getChannels());
        channels.forEach(channelId -> this.removeUserFromChannel(channelId, userId));
        user.softDelete();
    }
}
