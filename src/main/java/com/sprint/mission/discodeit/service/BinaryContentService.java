package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(BinaryContentCreateRequest request);

    BinaryContentDto find(UUID binaryContentId);

    List<BinaryContentDto> findAll();

    List<BinaryContentDto> findAllByUserId(UUID userId);

    List<BinaryContentDto> findAllByMessageId(UUID messageId);

    void delete(UUID binaryContentId);
}
