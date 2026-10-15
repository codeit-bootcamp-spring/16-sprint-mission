package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel createChannel(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel Name cannot be null or empty");
        }

        Channel channel = new Channel(channelName);
        this.data.add(channel);
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channelList = this.data.stream()
                .toList();

        return channelList;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = this.data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException());

        return channel;
    }

    @Override
    public Channel updateChannelName(UUID id, String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel Name cannot be null or empty");
        }

        Channel channel = this.findById(id);
        channel.updateChannelName(channelName);

        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = this.findById(id);
        this.data.remove(channel);
    }
}
