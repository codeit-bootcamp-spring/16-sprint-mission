package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusListResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;

import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);

    ReadStatusResponse find(UUID readStatusId);

    ReadStatusListResponse findAllByUserId(UUID userId);

    ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request);

    void delete(UUID readStatusId);
}
