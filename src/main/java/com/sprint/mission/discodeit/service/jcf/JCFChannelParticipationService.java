package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelParticipationService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelParticipationService
        implements ChannelParticipationService {

    // Channel ID를 키로 사용하고,
    // 해당 채널에 참여한 User ID들을 Set으로 관리합니다.
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
        // 존재하지 않는 사용자라면 UserService가 예외를 던집니다.
        userService.findById(userId);
        // 존재하지 않는 채널이라면 ChannelService가 예외를 던집니다.
        channelService.findById(channelId);

        // computeIfAbsent:
        // "키가 없을 때만 값을 만들어 등록"하는 Map 메서드
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

        // 마지막 참여자가 탈퇴한 경우 비어 있는 Set을 Map에 남길 필요가 없어
        // 채널 키도 제거합니다.
        if (participantIds.isEmpty()) {
            participantIdsByChannelId.remove(channelId);
        }
    }

    @Override
    public List<UUID> findParticipantIds(UUID channelId) {
        // 존재하지 않는 채널 조회를 명확한 예외로 처리
        channelService.findById(channelId);

        // List.copyOf()는 내부 Set이 외부에서 수정되지 않도록 복사본을 반환
        return List.copyOf(
                participantIdsByChannelId.getOrDefault(channelId, Set.of())
        );
    }

    @Override
    public List<UUID> findChannelIds(UUID userId) {
        // 존재하지 않는 사용자에 대한 참여 관계 조회를 막기
        userService.findById(userId);

        // stream/filter/map은 목록을 조건에 따라 찾고 변환하는 Stream API
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
