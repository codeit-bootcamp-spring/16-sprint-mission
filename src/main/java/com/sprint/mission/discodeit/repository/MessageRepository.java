package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    // 생성 + 수정
    Message save(Message message);
    // 단건 조회
    Optional<Message> findByMessageId(UUID id);
    // 전체 조회
    List<Message> findAll();
    // 삭제
    boolean existsByMessageId(UUID id);
    // 존재 여부 검사
    void deleteByMessageId(UUID id);
}