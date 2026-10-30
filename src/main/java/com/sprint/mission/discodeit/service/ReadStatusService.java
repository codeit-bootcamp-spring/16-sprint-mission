package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(ReadStatusCreateRequest request);
    ReadStatusDto findById(UUID id);
    List<ReadStatusDto> findAllByUserId(UUID userId);
    ReadStatusDto update(UUID id, ReadStatusUpdateRequest request);
    void delete(UUID id);

}
