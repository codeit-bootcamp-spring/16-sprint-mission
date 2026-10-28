package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 생성 기능(채널 생성)
    Channel createChannel(Channel channel);

    // 단건 조회 기능(채널 id 조회)
    Channel readChannel(UUID id);

    // 전체 조회 기능
    List<Channel> readAllChannel();

    // 수정 기능(채널 id 및 채널이름 수정)
    Channel updateChannel(UUID id, String name);

    // 삭제 기능(채널 id를 찾아서 채널 삭제)
    void deleteChannel(UUID id);

    // 서비스 간 의존성 주입 메서드(채널<->유저)
    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);

    boolean isMember(UUID channelId, UUID userId);
}
