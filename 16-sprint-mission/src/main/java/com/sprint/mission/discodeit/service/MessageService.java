package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public interface MessageService {
    Message create(String message, String channelId, String userId);

    Message read(String id) throws Exception;

    void update(String id, String newMessage) throws Exception;

    Message delete(String id);
}
