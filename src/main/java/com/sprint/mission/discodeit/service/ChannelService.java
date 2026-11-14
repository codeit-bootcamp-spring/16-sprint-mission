package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelResponseDto createPublicChannel(PublicChannelCreateRequestDto dto);

    ChannelResponseDto createPrivateChannel(PrivateChannelCreateRequestDto dto);

    // 채널 입장
    ChannelResponseDto joinChannel(UUID channelId, UUID userId);

    // 채널 퇴장
    void leaveChannel(UUID channelId, UUID userId);

    ChannelResponseDto findChannel(UUID channelId);

    List<ChannelDto> findAccessibleChannelsByUserId(UUID userId);

    ChannelResponseDto updateChannel(UUID channelId, ChannelUpdateRequestDto dto);

    void deleteChannel(UUID channelId);
}