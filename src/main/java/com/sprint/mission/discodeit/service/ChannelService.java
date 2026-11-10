package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // 공개 채널 생성
    ChannelDto createPublic(PublicChannelCreateRequest request);

    // 비공개 채널 생성
    ChannelDto createPrivate(PrivateChannelCreateRequest request);

    // 채널 단건 조회
    ChannelDto find(UUID channelId);

    // 전체 채널 조회
    List<ChannelDto> findAll();

    // 사용자가 참여한 채널 조회
    List<ChannelDto> findAllByUserId(UUID userId);

    // 채널 수정
    ChannelDto update(ChannelUpdateRequest request);

    // 채널 삭제
    void delete(UUID channelId);
}
