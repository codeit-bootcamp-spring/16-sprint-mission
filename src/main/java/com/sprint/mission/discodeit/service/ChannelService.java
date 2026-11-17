package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublicChannel(PublicChannelCreateRequest request);

    ChannelDto createPrivateChannel(PrivateChannelCreateRequest request);

    // ChannelResponse find(ChannelFindRequest request);

    ChannelListResponse findAllByUserId(UUID userId);

    ChannelDto update(UUID channelId, ChannelUpdateRequest request);

    void delete(UUID channelId);
}
