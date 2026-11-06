package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // Channel createPublicChannel(PublicChannelCreateRequest request);
    // Channel createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelInfo createChannel(ChannelCreateRequest request);
    // Instant findLastMessageAt(UUID channelId);
    ChannelInfo find(ChannelFindRequest request);
    // List<ChannelInfo> findAll();
    List<ChannelInfo> findAllByUserId(UUID userId);
    ChannelInfo update(UUID channelId, ChannelUpdateRequest request);
    void delete(UUID channelId);
}
