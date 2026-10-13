package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel (String name);

    Optional<Channel> findById(UUID id);

    Optional<Channel> findByName(String name);

    List<Channel> findAll();

    Channel updateChannel(UUID id, String name);

    void deleteChannel(UUID id);
}
