package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface ChannelParticipationService {
    void joinChannel(UUID userId, UUID channelId);

    void leaveChannel(UUID userId, UUID channelId);

    List<UUID> findParticipantIds(UUID channelId);

    List<UUID> findChannelIds(UUID userId);

    // 사용자가 참여 중인 모든 채널 관계를 제거
    void removeUserFromAllChannels(UUID userId);

    // 채널의 모든 참여 관계를 제거
    void removeAllParticipants(UUID channelId);
}
