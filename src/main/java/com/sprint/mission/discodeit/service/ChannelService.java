package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelInfo createChannel(ChannelCreateRequest request);
    ChannelInfo find(ChannelFindRequest request);
    List<ChannelInfo> findAllByUserId(UUID userId);
    ChannelInfo update(UUID channelId, ChannelUpdateRequest request);
    void delete(UUID channelId);
}
