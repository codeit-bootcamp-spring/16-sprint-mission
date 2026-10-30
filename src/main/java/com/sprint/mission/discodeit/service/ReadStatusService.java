package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus create(CreateReadStatusRequest request);

    ReadStatus findById(UUID id);

    List<ReadStatus> findAllByUserId(UUID userId);

    ReadStatus update(UpdateReadStatusRequest request);

    void delete(UUID id);
}