package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentResponseDto createReadStatus(BinaryContentCreateRequestDto dto);

    BinaryContentResponseDto findReadStatus(UUID binaryContentId);

    List<BinaryContentResponseDto> findAllReadStatusesByUserId(
            List<UUID> binaryContentIds
    );

    void deleteReadStatus(UUID binaryContentId);
}