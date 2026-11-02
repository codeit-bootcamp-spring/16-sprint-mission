package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.BinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    public BinaryContent save(BinaryContent binaryContent);
    public void deleteById(UUID id);
    public Optional<BinaryContent> findById(UUID id);
}
