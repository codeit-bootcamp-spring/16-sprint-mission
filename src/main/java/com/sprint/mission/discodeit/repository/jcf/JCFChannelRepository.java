package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public Channel findById(UUID channelId) {
        return channels.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Channel save(Channel channel) {
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public void deleteById(UUID channelId) {
        channels.remove(channelId);
    }

    @Override
    public List<Channel> findByCreatorId(UUID creatorId) {
        return channels.values().stream()
                .filter(channel -> channel.getCreator().getId().equals(creatorId))
                .toList();
    }

    @Override
    public List<Channel> findByParticipantId(UUID userId) {
        return channels.values().stream()
                .filter(channel -> channel.getChannelParticipants().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }
}