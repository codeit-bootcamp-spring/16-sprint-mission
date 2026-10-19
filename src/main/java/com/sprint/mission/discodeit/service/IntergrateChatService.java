package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface IntergrateChatService {
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
    void deleteUserCompletely(UUID userId);
    void deleteChannelCompletely(UUID channelId);
}
