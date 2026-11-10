package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(CreateReadStatusRequest request);
    ReadStatusResponse find(UUID statusId);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusResponse update(UpdateReadStatusRequest request);
    void delete(UUID statusId);
}
