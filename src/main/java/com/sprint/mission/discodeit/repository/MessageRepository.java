package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message create(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Message update(Message message);

    // 여러 Message를 한 번에 갱신한다. 저장 비용이 큰 구현체는 한 번만 저장한다.
    void updateAll(List<Message> messages);

    void delete(UUID id);
}
