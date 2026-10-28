package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Message 데이터를 저장하고 조회하는 방법의 약속입니다. */
public interface MessageRepository {
    /** ID가 같은 메시지가 있으면 교체하고, 없으면 새로 저장합니다. */
    void save(Message message);

    /** ID로 조회하며, 결과가 없으면 Optional.empty()를 반환합니다. */
    Optional<Message> findById(UUID id);

    /** 저장된 모든 메시지를 돌려줍니다. */
    List<Message> findAll();

    /** ID에 해당하는 메시지가 있는지 확인합니다. */
    boolean existsById(UUID id);

    /** 메시지를 삭제하고 실제 삭제 여부를 반환합니다. */
    boolean deleteById(UUID id);
}
