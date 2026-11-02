package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    // 생성 및 수정
    Message save(Message message);

    // 단건 조회
    Optional<Message> findByMessageId(UUID id);

    // 전체 조회
    List<Message> findAll();

    // 특정 채널의 메시지 조회
    List<Message> findAllByChannelId(UUID channelId);

    // 삭제
    void deleteByMessageId(UUID id);

    // 특정 채널의 메시지 전체 삭제
    void deleteByChannelId(UUID channelId);

    // 존재 여부 검사
    boolean existsByMessageId(UUID id);
}