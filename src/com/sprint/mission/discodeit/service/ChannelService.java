package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    // PUBLIC 채널 생성
    ChannelDto createPublic(ChannelCreateRequest request);

    // PRIVATE 채널 생성
    ChannelDto createPrivate(PrivateChannelCreateRequest request);

    // 채널 단건 조회
    Optional<ChannelDto> findById(UUID id);

    // 전체 채널 조회
    List<ChannelDto> findAll();

    // 특정 사용자가 참여한 채널 조회
    List<ChannelDto> findAllByUserId(UUID userId);

    // 채널 수정
    ChannelDto update(ChannelUpdateRequest request);

    // 채널 삭제
    void delete(UUID id);

    // 채널 참여 및 나가기
    void join(UUID channelId, UUID userId);

    void leave(UUID channelId, UUID userId);
}