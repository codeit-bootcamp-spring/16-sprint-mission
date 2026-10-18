package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

public JCFChannelService() {
 this.data =new HashMap<>();
}

    @Override
    public Channel create(String channelName, String topic, String type) {
        Channel channel = new Channel(channelName, topic, type);
        data.put(channel.getId(), channel);
        return channel;
    }
    @Override
    public Channel read(UUID id) {
        return data.get(id);
    }
    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data.values());
    }
    @Override
    public Channel update(UUID id, String channelName, String topic, String type) {
        Channel channel = data.get(id);
        channel.update(channelName, topic, type);
        return channel;
    }
    @Override
    public void addParticipant(UUID channelId, UUID userId) {
        Channel channel = data.get(channelId);
        channel.addParticipant(userId);
    }

    @Override
    public void removeParticipant(UUID channelId, UUID userId) {
        Channel channel = data.get(channelId);
        channel.removeParticipant(userId);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
