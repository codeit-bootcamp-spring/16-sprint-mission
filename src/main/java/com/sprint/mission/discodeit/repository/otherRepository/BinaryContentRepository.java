package com.sprint.mission.discodeit.repository.otherRepository;

import com.sprint.mission.discodeit.entity.newDomain.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    void save(BinaryContent binaryContent);

    Optional<BinaryContent> findById(UUID id);

    void deleteById(UUID id);

    List<BinaryContent> findAllByIdIn(List<UUID> ids);
}