package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPrivate(PrivateChannelCreateRequest request);
    ChannelDto createPublic(PublicChannelCreateRequest request);
    ChannelDto findById(UUID id);
    List<ChannelDto> findAll();
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto update(UUID id, ChannelUpdateRequest request);
    void delete(UUID id, UUID requesterId);

    void join(UUID id, UUID userId);
    void leave(UUID id, UUID userId);
    void transferOwnership(UUID id, UUID currentOwnerId, UUID newOwnerId);
}
