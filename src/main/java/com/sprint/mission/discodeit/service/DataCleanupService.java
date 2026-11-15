package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface DataCleanupService {

    void deleteUserData(UUID userId);

    void deleteMessageData(UUID messageId);

    void deleteChannelData(UUID channelId);
}
