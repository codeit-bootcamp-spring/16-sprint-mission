package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel createChannel(String name) {
        Channel channel = new Channel(name);
        channels.add(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return channels.stream()
                .filter(channel -> channel.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return channels;
    }

    @Override
    public Channel updateChannel(UUID id, String name) {
        Channel channel = findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
        );
        channel.update(name);
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 ID 입니다.")
        );
        channels.remove(channel);
    }

}
