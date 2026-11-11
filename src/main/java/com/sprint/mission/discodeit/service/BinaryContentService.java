package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentRequest request);

    BinaryContentResponse find(UUID contentId);

    List<BinaryContentResponse> findAllByIdIn(List<UUID> contentIds);

    void delete(UUID contentId);
}
