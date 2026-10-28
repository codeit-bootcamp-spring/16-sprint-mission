package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    Channel create(String name, String description);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String name, String description);

    void delete(UUID id);

    // 채널의 참여자 목록만 수정
    void addMember(UUID channelId, UUID userId);

    // 채널 참여 요청
    void join(UUID channelId, UUID userId);

    // 채널 나가기
    void leave(UUID channelId, UUID userId);
}