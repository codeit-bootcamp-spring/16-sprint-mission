package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel save(Channel channel) {
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getId().equals(channel.getId())) {
                channels.set(i, channel);
                return channel;
            }
        }

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
        return List.copyOf(channels);
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return channels.stream()
                .filter(channel ->
                        channel.getUsers().stream()
                                .anyMatch(user ->
                                        user.getId().equals(userId)))
                .toList();
    }

    @Override
    public void delete(Channel channel) {
        channels.removeIf(existingChannel ->
                existingChannel.getId().equals(channel.getId()));
    }
}