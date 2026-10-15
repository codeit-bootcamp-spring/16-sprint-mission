package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 생성
    Channel save(Channel channel);
    // 조회
    List<Channel> findId(UUID id);
    // 전체조회
    List<Channel> findAll();
    //findId랑 비슷함
    List<Message> findMessagesByChannelId(UUID channelId);
    // 수정 (채널명 및 설명 수정)
    Channel update(UUID id, String name);
    // 삭제
    void deleteById(UUID id);
    //채널 관련
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}
