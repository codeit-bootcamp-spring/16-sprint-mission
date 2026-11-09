package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDownloadDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(BinaryContentCreateRequest request);

    Optional<BinaryContentDto> findById(UUID id);

    // 원본 BinaryContent 엔티티 조회
    Optional<BinaryContent> findEntityById(UUID id);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    // 파일 하나의 실제 데이터 조회
    Optional<BinaryContentDownloadDto> downloadById(UUID id);

    // 여러 파일의 실제 데이터 조회
    List<BinaryContentDownloadDto> downloadAllByIdIn(List<UUID> ids);

    void deleteById(UUID id);
}