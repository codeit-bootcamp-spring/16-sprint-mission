package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelParticipationService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelParticipationService
        implements ChannelParticipationService {

    private final Map<UUID, Set<UUID>> participantIdsByChannelId =
            new HashMap<>();

    private final UserService userService;
    private final ChannelService channelService;

    public JCFChannelParticipationService(
            UserService userService,
            ChannelService channelService
    ) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        userService.findById(userId);
        channelService.findById(channelId);

        Set<UUID> participantIds = participantIdsByChannelId
                .computeIfAbsent(channelId, id -> new java.util.HashSet<>());

        // 이미 참여한 사용자의 중복 참여를 거부
        if (!participantIds.add(userId)) {
            throw new IllegalArgumentException("이미 채널에 참여한 사용자입니다.");
        }
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        // 잘못된 사용자나 채널 ID를 정상적인 탈퇴로 처리하지 않도록 확인
        userService.findById(userId);
        channelService.findById(channelId);

        Set<UUID> participantIds =
                participantIdsByChannelId.get(channelId);

        if (participantIds == null || !participantIds.remove(userId)) {
            throw new IllegalArgumentException("채널 참여자를 찾을 수 없습니다.");
        }

        // 마지막 참여자가 나가면 빈 참여자 목록을 Map에 남기지 않습니다.
        if (participantIds.isEmpty()) {
            participantIdsByChannelId.remove(channelId);
        }
    }

    @Override
    public List<UUID> findParticipantIds(UUID channelId) {
        channelService.findById(channelId);

        return List.copyOf(
                participantIdsByChannelId.getOrDefault(channelId, Set.of())
        );
    }

    @Override
    public List<UUID> findChannelIds(UUID userId) {
        userService.findById(userId);

        return participantIdsByChannelId.entrySet().stream()
                .filter(entry -> entry.getValue().contains(userId))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public void removeUserFromAllChannels(UUID userId) {
        for (UUID channelId : findChannelIds(userId)) {
            leaveChannel(userId, channelId);
        }
    }

    @Override
    public void removeAllParticipants(UUID channelId) {
        for (UUID userId : findParticipantIds(channelId)) {
            leaveChannel(userId, channelId);
        }
    }

}
