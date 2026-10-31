
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    // 특정 채널에 속한 메시지 조회
    List<Message> findAllByChannelId(UUID channelId);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}

