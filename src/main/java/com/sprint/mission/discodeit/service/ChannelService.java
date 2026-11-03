package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, String description);
    Channel createPublicChannel(PublicChannelCreateDto dto);
    Channel createPrivateChannel(PrivateChannelCreateDto dto);
    ChannelResponseDto find(UUID channelId);
    List<Channel> findAll();
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    Channel update(ChannelUpdateDto dto);
    void delete(UUID channelId);
}
