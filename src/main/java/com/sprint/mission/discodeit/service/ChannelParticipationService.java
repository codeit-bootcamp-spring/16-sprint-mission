package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface ChannelParticipationService {
    void joinChannel(UUID userId, UUID channelId);

    void leaveChannel(UUID userId, UUID channelId);

    List<UUID> findParticipantIds(UUID channelId);

    List<UUID> findChannelIds(UUID userId);

    void removeUserFromAllChannels(UUID userId);

    void removeAllParticipants(UUID channelId);
}
