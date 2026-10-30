package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel createPublic(PublicChannelCreateRequest request);

    Channel createPrivate(PrivateChannelCreateRequest request);

    ChannelDto findById(UUID id);

    List<ChannelDto> findAllByUserId(UUID userId);

    void update(ChannelUpdateRequest request);

    void delete(UUID id);

}
