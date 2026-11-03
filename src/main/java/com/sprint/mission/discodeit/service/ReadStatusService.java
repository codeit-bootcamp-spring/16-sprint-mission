package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusResponse create(CreateReadStatusRequest request);

    ReadStatusResponse findById(UUID id);

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    ReadStatusResponse update(UpdateReadStatusRequest request);

    void delete(UUID id);
}