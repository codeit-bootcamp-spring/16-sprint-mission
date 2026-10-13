package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 생성 기능
    Message createMessage(Message message);

    // 단건 조회 기능
    Message readMessage(UUID id);

    // 전체 조회 기능
    List<Message> readAllMessage();

    // 수정 기능
    Message updateMessage(UUID id, Message message);

    // 삭제 기능
    Message deleteMessage(UUID id);
}