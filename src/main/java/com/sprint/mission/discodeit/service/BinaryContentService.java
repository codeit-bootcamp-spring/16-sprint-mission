package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentResponse create(
            CreateBinaryContentRequest request
    );

    BinaryContentResponse findById(UUID id);

    List<BinaryContentResponse> findAllByIdIn(
            Collection<UUID> ids
    );

    BinaryContent findEntityById(UUID id);

    byte[] readBytes(UUID id);

    void delete(UUID id);
}