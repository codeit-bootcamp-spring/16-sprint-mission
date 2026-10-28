package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // 생성
    Channel createChannel(Channel channel);

    // 참여/퇴장
    Channel joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);

    // 조회
    Channel getChannelById(UUID channelId);
    List<Channel> getAllChannels();
    List<Channel> getChannelsByCreatorId(UUID creatorId);
    List<Channel> getChannelsByParticipantId(UUID userId);
    List<User> getParticipants(UUID channelId);
    boolean isParticipant(UUID channelId, UUID userId);

    // 수정
    Channel updateChannelName(UUID channelId, UUID userId, String channelName);

    // 삭제
    void deleteChannel(UUID channelId, UUID userId);

}
