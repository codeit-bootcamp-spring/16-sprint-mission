package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(BinaryContentCreateRequest request);
    BinaryContentDto findById(UUID id);
    List<BinaryContentDto> findAllByIdIn(List<UUID> Ids);
    void delete(UUID id);
}
