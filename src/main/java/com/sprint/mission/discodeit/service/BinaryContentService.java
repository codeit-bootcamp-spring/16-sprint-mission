package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentResponseDto createBinaryContent(BinaryContentCreateRequestDto dto);

    BinaryContentResponseDto findBinaryContent(UUID binaryContentId);

    List<BinaryContentResponseDto> findAllBinaryContentsByIds(
            List<UUID> binaryContentIds
    );

    void deleteBinaryContent(UUID binaryContentId);
}