package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel findById(UUID channelId);
    List<Channel> findAll();
    Channel save(Channel channel);
    void deleteById(UUID channelId);

    List<Channel> findByCreatorId(UUID creatorId);
    List<Channel> findByParticipantId(UUID userId);
}
