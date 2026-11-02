package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusDto create(ReadStatusCreateRequest request);

    ReadStatusDto find(UUID readStatusId);

    List<ReadStatusDto> findAll();

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto update(ReadStatusUpdateRequest request);

    void delete(UUID readStatusId);
}
