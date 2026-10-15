package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
Message create(UUID channelId, UUID senderUser, String content);
Message read(UUID id);
List <Message> readAll();
Message update(UUID id, String content);
void delete(UUID id);

}
