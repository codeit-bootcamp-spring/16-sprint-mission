package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findAll();

    List<ReadStatus> findAllByUserId(UUID userId);

    // 특정 사용자와 채널의 참여 관계 조회
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}