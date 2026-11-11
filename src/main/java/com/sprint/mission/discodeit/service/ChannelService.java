package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDto createPublicChannel(PublicChannelCreateDto dto);

    ChannelDto createPrivateChannel(PrivateChannelCreateDto dto);

    ChannelDto findById(UUID id);

    List<ChannelDto> findAll();

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto updateChannel(ChannelUpdateDto dto);

    void deleteChannel(UUID id);
}
