package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.channels.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channels.PublicChannelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    /* PUBLIC 채널 생성 맟 PRIVATE 채널 생성 */
    ChannelDto createPublicChannel(PublicChannelCreateRequest request);

    ChannelDto createPrivateChannel(PrivateChannelCreateRequest request);

    // 단건 조회 기능(채널 ID 조회)
    ChannelDto readChannel(UUID id);

    // 특정 유저가 볼 수 있는 채널 조회
    List<ChannelDto> findAllByUserId(UUID userId);

    // 수정 기능(채널 ID 및 채널이름 수정)
    ChannelDto updateChannel(UUID channelId, PublicChannelUpdateRequest request);

    // 삭제 기능(채널 ID를 찾아서 채널 삭제)
    void deleteChannel(UUID id);

    /* 서비스 간 의존성 주입 메서드(채널<->유저) */
    /* 채널 참여, 채널 퇴장, 참여 여부 확인 */
    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);

    boolean isMember(UUID channelId, UUID userId);
}