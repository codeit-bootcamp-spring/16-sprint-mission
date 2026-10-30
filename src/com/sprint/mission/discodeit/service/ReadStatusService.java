package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus create(ReadStatusCreateRequest request);

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findAllByUserId(UUID userId);

    ReadStatus update(ReadStatusUpdateRequest request);

    void delete(UUID id);
}