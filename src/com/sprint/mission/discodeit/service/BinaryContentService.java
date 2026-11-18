package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(BinaryContentCreateRequest request);

    BinaryContentDto create(MultipartFile file);

    Optional<BinaryContentDto> findById(UUID id);

    Optional<BinaryContent> findEntityById(UUID id);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    void deleteById(UUID id);
}