package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.response.PrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.FindChannelResponse;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(CreatePublicChannelRequest request);
    PrivateChannelResponse createPrivateChannel(CreatePrivateChannelRequest request);
    FindChannelResponse find(UUID channelId);
    List<FindChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(UUID channelId, UpdateChannelRequest request);
    void delete(UUID channelId);
}
