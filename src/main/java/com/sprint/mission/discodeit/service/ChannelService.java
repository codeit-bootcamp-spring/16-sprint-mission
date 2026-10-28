package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // 채널 등록
    void create(Channel channel);

    /** 템플릿용 편의 API: 채널 정보를 받아 생성·등록하고 생성된 객체를 돌려줍니다. */
    default Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        create(channel);
        return channel;
    }

    // 채널 조회
    Channel findById(UUID uuid);

    // 모든 채널 조회
    List<Channel> findAll();

    // 채널 수정
    void update(UUID id, Channel channel);

    // 채널 삭제
    void delete(UUID id);

}
