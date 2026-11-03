package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponse createPublic(
            CreatePublicChannelRequest request
    );

    ChannelResponse createPrivate(
            CreatePrivateChannelRequest request
    );

    ChannelResponse findById(UUID id);

    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse update(UpdateChannelRequest request);

    void delete(UUID id);

    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);
}