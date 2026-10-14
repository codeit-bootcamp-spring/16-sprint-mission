package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, UUID requesterId) {
        Channel channel = findById(id);

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }

        if (!channel.getOwnerId().equals(requesterId)) {
            throw new IllegalArgumentException("채널 수정 권한이 없습니다.");
        }

        data.put(id, channel);
        return channel;
    }

    @Override
    public void delete(UUID id, UUID requesterId) {
        Channel channel = findById(id);

        if(!channel.getOwnerId().equals(requesterId)) {
            throw new IllegalArgumentException("채널 삭제 권한이 없습니다.");
        }

        data.remove(id);
    }
}
