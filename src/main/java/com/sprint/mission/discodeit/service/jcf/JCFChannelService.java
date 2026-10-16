package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name, new HashMap<>(), new LinkedHashMap<>());
        this.data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = this.findById(id).orElseThrow(() -> new NoSuchElementException("수정할 채널이 존재하지 않습니다."));
        this.validateNotDeleted(channel);

        return channel.updateName(name);
    }

    @Override
    public void addUserToChannel(UUID channelId, User user) {
        // 유저가 존재하는지 확인
        this.userService.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("채널에 추가할 유저가 존재하지 않습니다."));
        this.userService.validateNotDeleted(user);

        Channel channel = this.findById(channelId).orElseThrow(() -> new NoSuchElementException("유저를 추가할 채널이 존재하지 " +
                "않습니다."));
        this.validateNotDeleted(channel);

        channel.addUser(user);

        // 채널에 유저 추가 시 유저에도 채널 추가
        user.addChannel(channel);
    }

    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        // 유저가 존재하는지 확인
        User user = this.userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("채널에서 탈퇴시킬 유저가 존재하지 않습니다."));
        this.userService.validateNotDeleted(user);

        Channel channel = this.findById(channelId).orElseThrow(() -> new NoSuchElementException("유저가 탈퇴할 채널이 " +
                "존재하지 않습니다."));
        this.validateNotDeleted(channel);

        // 채널의 유저 탈퇴 시 유저에도 채널 지우기
        channel.getUsers().values()
                .stream()
                .filter(joinedUser -> joinedUser.getId().equals(userId))
                .findFirst()
                .ifPresent(joinedUser -> joinedUser.removeChannel(channelId));

        channel.removeUser(userId);
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
        Channel channel = this.findById(id).orElseThrow(() -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        this.validateNotDeleted(channel);

        // 채널 삭제 시 유저에도 채널 삭제
        channel.getUsers().values().forEach(user -> user.removeChannel(id));

        channel.softDelete();
    }

    public void validateNotDeleted(Channel channel) {
        if (channel.isDeleted()) throw new IllegalStateException("삭제된 채널입니다.");
    }
}
