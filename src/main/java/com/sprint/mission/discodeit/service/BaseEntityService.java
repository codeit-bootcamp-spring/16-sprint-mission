package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseEntityService<T extends BaseEntity> {
    T update(T entity);

    Optional<T> findById(UUID id);

    Optional<List<T>> findAll();

    void delete(UUID id);
}
