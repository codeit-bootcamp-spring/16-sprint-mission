package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels = new ConcurrentHashMap<>();

    @Override
    public Channel save(Channel channel) {
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(channels.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(channels.values());
    }

    @Override
    public void delete(Channel channel) {
        channels.remove(channel.getId());
    }
}