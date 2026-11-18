package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublic(PublicChannelCreateRequest createRequest);

    ChannelResponse createPrivate(PrivateChannelCreateRequest createRequest);

    ChannelResponse update(UUID id, PublicChannelUpdateRequest updateRequest);

    void addUserToChannel(UUID channelId, UUID userId);

    void removeUserFromChannel(UUID channelId, UUID userId);

    ChannelFindResponse findById(UUID id);

    List<ChannelFindResponse> findAllByUserId(UUID userId);

    void delete(UUID id);
}
