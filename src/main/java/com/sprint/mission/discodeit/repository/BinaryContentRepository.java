package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);

    List<BinaryContent> findAll();

    Optional<BinaryContent> findById(UUID id);

    void delete(BinaryContent binaryContent);
}
