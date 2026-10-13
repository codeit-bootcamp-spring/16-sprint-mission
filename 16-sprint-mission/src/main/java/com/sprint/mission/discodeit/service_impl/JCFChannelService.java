package com.sprint.mission.discodeit.service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(ChannelType channelType, String title, String description) {
        Channel newChannel = new Channel(channelType, title, description);
        data.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
            return null;
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateChannelType(UUID id, ChannelType newChannelType) {
        Channel channel = findById(id);
        if (channel == null) {
            System.out.println("존재하지 않는 채널의 타입을 바꿀 수 없습니다.");
            return;
        }
        channel.setChannelType(newChannelType);
    }

    @Override
    public void updateTitle(UUID id, String newTitle) {
        Channel channel = findById(id);
        if (channel == null) {
            System.out.println("존재하지 않는 채널의 제목을 바꿀 수 없습니다.");
            return;
        }
        channel.setTitle(newTitle);
    }

    @Override
    public void updateDescription(UUID id, String newDescription) {
        Channel channel = findById(id);
        if (channel == null) {
            System.out.println("존재하지 않는 채널의 설명을 바꿀 수 없습니다.");
            return;
        }
        channel.setDescription(newDescription);
    }

    @Override
    public Channel delete(UUID id) {
        Channel removed = data.remove(id);
        if (removed == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
            return null;
        }
        return removed;
    }
}
