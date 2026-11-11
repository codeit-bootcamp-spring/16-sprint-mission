package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    BinaryContent save(BinaryContent entity);

    BinaryContent findById(UUID id);

    List<BinaryContent> findAll();

    void delete(UUID id);

    default List<BinaryContent> findAllByIdIn(Collection<UUID> ids) {
        return findAll().stream()
                .filter(content -> ids.contains(content.getId()))
                .toList();
    }
}