package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublic(PublicChannelRequest request);
    Channel createPrivate(PrivateChannelRequest request);
    ChannelDto findById(UUID id);
    List<ChannelDto> findAllByUserId(UUID userId);
    Channel update(UUID id, String name);
    void delete(UUID id);

    void join(UUID channelId, UUID userId);
}
