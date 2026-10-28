package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Channel 데이터를 저장하고 조회하는 방법의 약속입니다. */
public interface ChannelRepository {
    /** ID가 같은 채널이 있으면 교체하고, 없으면 새로 저장합니다. */
    void save(Channel channel);

    /** ID로 조회하며, 결과가 없으면 Optional.empty()를 반환합니다. */
    Optional<Channel> findById(UUID id);

    /** 저장된 모든 채널을 돌려줍니다. */
    List<Channel> findAll();

    /** ID에 해당하는 채널이 있는지 확인합니다. */
    boolean existsById(UUID id);

    /** 채널을 삭제하고 실제 삭제 여부를 반환합니다. */
    boolean deleteById(UUID id);
}
