package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }


    @Override
    public Channel create(String name) {
        Channel  channel = new Channel(name);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.stream().filter(channel -> channel.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return data.stream().collect(Collectors.toList());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);
        if (channel == null) {
            return null;
        }
        channel.update(name);
        return channel;
    }

    @Override
    public void delete(UUID id) {

        data.removeIf(channel -> channel.getId().equals(id));

    }

}
