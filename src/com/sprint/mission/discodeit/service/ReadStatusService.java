package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(ReadStatusCreateRequest request);
    Optional<ReadStatusDto> findById(UUID id);
    List<ReadStatusDto> findAllByUserId(UUID userId);
    ReadStatusDto update(ReadStatusUpdateRequest request);
    void delete(UUID id);
}
