package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentListResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreateRequest request);

    BinaryContentResponse find(UUID binaryContentId);

    BinaryContentListResponse findAllByIdIn(List<UUID> BinaryContentIds);

    void delete(UUID id);
}
