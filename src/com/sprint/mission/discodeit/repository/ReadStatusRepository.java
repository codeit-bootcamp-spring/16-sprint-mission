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

    Optional<ReadStatus> findByUserIdAndChannelId(
            UUID userId,
            UUID channelId
    );

    void deleteById(UUID id);
}