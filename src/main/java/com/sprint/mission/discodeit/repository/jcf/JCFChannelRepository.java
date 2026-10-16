package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
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
    public Channel update(UUID id, ChannelType channelType, String title, String description) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다.");
        }
        channel.update(channelType, title, description);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 Channel입니다.");
        }
    }
}
