package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.Channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelPublicCreateResponse createPublic(ChannelPublicCreateRequest createRequest);

    ChannelPrivateCreateResponse createPrivate(ChannelPrivateCreateRequest createRequest);

    ChannelUpdateResponse update(ChannelUpdateRequest updateRequest);

    void addUserToChannel(UUID channelId, UUID userId);

    void removeUserFromChannel(UUID channelId, UUID userId);

    ChannelFindResponse findById(UUID id);

    List<ChannelFindResponse> findAllByUserId(UUID userId);

    void delete(UUID id);
}
