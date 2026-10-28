package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel add(Channel channel);

    Channel getById(UUID id);

    List<Channel> getAll();

    Channel update(UUID id, String name);

    void delete(UUID id);
}