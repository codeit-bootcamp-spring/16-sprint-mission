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
    //채널 메세지 조회
    List<Message> findMessagesByChannelId(UUID channelId);
    //엔티티 조회
    Channel findEntityChannel(UUID id);
    // 수정 (채널명 및 설명 수정)
    Channel update(UUID id, String name);
    // 삭제
    void deleteById(UUID id);
    //채널 관련
}
