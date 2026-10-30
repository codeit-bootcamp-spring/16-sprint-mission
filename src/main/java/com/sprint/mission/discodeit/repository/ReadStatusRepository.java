package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    Optional<ReadStatus> findById(UUID readStatusId);

    List<ReadStatus> findAll();

    ReadStatus save(ReadStatus readStatus);

    void deleteById(UUID readStatusId);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    List<ReadStatus> findByUserId(UUID userId);

    List<ReadStatus> findByChannelId(UUID channelId);
}
