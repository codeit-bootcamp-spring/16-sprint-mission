package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import java.util.List;
import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;


public interface MessageService {

    Message add(Message message);

    Message getById(UUID id);

    List<Message> getAll();

    Message update(UUID id, String content);

    void delete(UUID id);

    default MessageResponse create(MessageCreateRequest request) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default MessageResponse update(MessageUpdateRequest request) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default MessageResponse find(UUID id) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default List<MessageResponse> findAllByChannelId(UUID channelId) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

}

