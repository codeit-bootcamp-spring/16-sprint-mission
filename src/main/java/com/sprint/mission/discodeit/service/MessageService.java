
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    void create(Message message);

    /** 템플릿용 편의 API. 인자 순서는 content, channelId, authorId입니다. */
    default Message create(String content, UUID channelId, UUID authorId) {
        // Message 생성자 순서는 content, userId(authorId), channelId이므로 변환합니다.
        Message message = new Message(content, authorId, channelId);
        create(message);
        return message;
    }

    Message findById(UUID id);

    List<Message> findAll();

    // void update(UUID id, Message message);
    void update(UUID id, String content);

    void delete(UUID id);

}
