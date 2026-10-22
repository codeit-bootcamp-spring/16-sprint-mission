package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널 정보는 필수입니다.");
        }

        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        data.remove(id);
    }
}
