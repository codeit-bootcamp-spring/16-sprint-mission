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

    // 수정 기능(채널 id 및 채널정보 수정)
    Channel updateChannel(UUID id, Channel channel);

    // 삭제 기능(채널 id를 찾아서 채널 삭제)
    Channel deleteChannel(UUID id);
}
