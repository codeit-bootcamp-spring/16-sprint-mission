package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Channel 데이터를 저장하고 조회하는 방법의 약속입니다. */
public interface ChannelRepository {

    void save(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    boolean existsById(UUID id);

    boolean deleteById(UUID id);
}
