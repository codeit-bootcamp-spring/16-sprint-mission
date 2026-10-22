package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    public List<Channel> channels;

    public JCFChannelRepository() {
        this.channels = new ArrayList<Channel>();
    }

    @Override
    public Channel save(Channel channel) {
        this.channels.add(channel);

        return channel;
    }

    @Override
    public List<Channel> getChannels() {
        return List.copyOf(this.channels);
    }

    @Override
    public Channel getChannel(UUID id) {
        Channel channel = this.channels.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 ID의 채널을 찾을 수 없습니다.: " + id
                ));

        return channel;
    }

    @Override
    public void removeChannel(UUID id) {
        Channel channel = this.getChannel(id);
        this.channels.remove(channel);
    }
}
