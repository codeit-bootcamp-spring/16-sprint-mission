
package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> getById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        data.remove(id);
    }
}