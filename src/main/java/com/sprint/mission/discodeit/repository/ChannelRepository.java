package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel create(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    // 이미 저장된 Channel의 변경된 상태를 저장한다. 저장되어 있지 않으면 NoSuchElementException을 던진다.
    Channel update(Channel channel);

    void delete(UUID id);
}
