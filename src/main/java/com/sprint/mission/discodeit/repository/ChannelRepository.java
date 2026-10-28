package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    // 생성 + 수정
    Channel save(Channel channel);
    // 단건 조회
    Optional<Channel> findByChannelId(UUID id);
    // 전체 조회
    List<Channel> findAll();
    // 삭제
    void deleteByChannelId(UUID id);
    // 존재 여부 검사
    boolean existsByChannelId(UUID id);
}