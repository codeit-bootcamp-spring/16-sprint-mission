package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    List<ReadStatus> saveAll(List<ReadStatus> readStatusList);
    Optional<ReadStatus> findById(UUID readStatusId);
    List<ReadStatus> findAll();
    List<ReadStatus> findAllByUserId(UUID userId);
    boolean existsById(UUID readStatusId);
    void deleteById(UUID id);
}
