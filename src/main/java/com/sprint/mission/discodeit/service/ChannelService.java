package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {


    // 생성
    Channel save(Channel channel);

    // 조회
    List<Channel> findId(UUID id);

    // 전체조회
    List<Channel> findAll();

    // 수정 (채널명 및 설명 수정)
    Channel update(UUID id, String name);

    // 삭제
    void deleteById(UUID id);
}
