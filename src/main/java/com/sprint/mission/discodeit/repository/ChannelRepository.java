
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    void save(Channel channel);

    Channel getById(UUID id);

    List<Channel> getAll();

    void delete(UUID id);
}
