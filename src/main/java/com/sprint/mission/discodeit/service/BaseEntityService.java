package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseEntityService<T extends BaseEntity> {

    Optional<T> findById(UUID id);

    Optional<List<T>> findAll();

    T delete(UUID id);
}
