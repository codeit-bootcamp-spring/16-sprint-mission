package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class UserChannelManager {
    private final JCFChannelService jcfChannelService;
    private final JCFUserService jcfUserService;

    public UserChannelManager(JCFUserService jcfUserService, JCFChannelService jcfChannelService) {
        this.jcfChannelService = jcfChannelService;
        this.jcfUserService = jcfUserService;
    }

    public void addChannelToUser(UUID userId, UUID channelId) {
        User user = jcfUserService.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        Channel channel =
                jcfChannelService.findById(channelId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        Map<UUID, Channel> channels = user.getChannels();
        channels.put(channel.getId(), channel);
        jcfUserService.update(userId, channels);

        // 유저에 채널 추가 시 채널에도 유저 추가
        Map<UUID, User> users = channel.getUsers();
        users.put(userId, user);
        jcfChannelService.update(channelId, users);
    }

    public void removeChannelFromUser(UUID userId, UUID channelId) {
        User user = jcfUserService.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        Channel removedChannel = user.getChannels().remove(channelId);
        if (removedChannel == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");

        // 유저에 채널 제거 시 채널에도 유저 제거
        User removedUser = removedChannel.getUsers().remove(userId);
        if (removedUser == null) throw new NoSuchElementException("삭제할 유저가 존재하지 않습니다.");
    }

    public void deleteChannel(UUID id) {
        Channel deletedChannel = jcfChannelService.delete(id);

        // 채널 삭제 시 모든 등록 유저들에서 채널 삭제
        deletedChannel.getUsers().values().forEach(user -> {
            Map<UUID, Channel> channels = user.getChannels();
            Channel removed = channels.remove(id);
            if (removed == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");
            jcfUserService.update(user.getId(), channels);
        });
    }

    public void addUserToChannel(UUID channelId, UUID userId) {
        Channel channel = jcfChannelService.findById(channelId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        User user = jcfUserService.findById(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        Map<UUID, User> users = channel.getUsers();
        users.put(userId, user);
        jcfChannelService.update(channelId, users);

        // 채널에 유저 추가 시 유저에도 채널 추가
        Map<UUID, Channel> channels = user.getChannels();
        channels.put(channelId, channel);
        jcfUserService.update(userId, channels);
    }

    public void removeUserFromChannel(UUID channelId, UUID userId) {
        Channel channel = jcfChannelService.findById(channelId).orElseThrow(() -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        User removedUser = channel.getUsers().remove(userId);
        if (removedUser == null) throw new NoSuchElementException("삭제할 유저가 존재하지 않습니다.");

        // 채널에 유저 제거 시 유저에도 체널 제거
        Channel removedChannel = removedUser.getChannels().remove(channelId);
        if (removedChannel == null) throw new NoSuchElementException("삭제할 채널이 존재하지 않습니다.");
    }

    public void deleteUser(UUID id) {
        User deletedUser = jcfUserService.delete(id);

        // 유저 삭제 시 모든 등록 채널에서 유저 삭제
        deletedUser.getChannels().values().forEach(channel -> {
            Map<UUID, User> users = channel.getUsers();
            if (users.remove(id) == null) throw new NoSuchElementException("삭제할 유저가 존재하지 않습니다.");
            jcfChannelService.update(channel.getId(), users);
        });
    }
}
