package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Message 데이터를 저장하고 조회하는 방법의 약속입니다. */
public interface MessageRepository {

    void save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    boolean existsById(UUID id);

    boolean deleteById(UUID id);
}
