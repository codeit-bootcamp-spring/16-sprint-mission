package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserChannelManager;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private UserChannelManager userChannelManager;

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
        this.userChannelManager.addUserToChannel(channelId, userId);
    }

    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        this.userChannelManager.removeUserFromChannel(channelId, userId);
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
        this.userChannelManager.deleteChannel(id);
    }

    public void setUserChannelManager(UserChannelManager userChannelManager) {
        this.userChannelManager = userChannelManager;
    }
}
