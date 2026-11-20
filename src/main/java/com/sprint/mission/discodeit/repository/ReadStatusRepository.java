package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    void save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findAll();

    boolean existsById(UUID id);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    boolean deleteById(UUID id);
}
