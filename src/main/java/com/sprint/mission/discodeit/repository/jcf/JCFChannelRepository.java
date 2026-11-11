package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
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
    public List<Channel> findAll() {
        return List.copyOf(channels);
    }

    @Override
    public void delete(Channel channel) {
        channels.removeIf(existingChannel ->
                existingChannel.getId().equals(channel.getId()));
    }
}