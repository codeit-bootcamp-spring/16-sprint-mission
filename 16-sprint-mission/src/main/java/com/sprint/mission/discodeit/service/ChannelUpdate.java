package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public interface ChannelUpdate {

    void updateChannelType(UUID id, ChannelType newChannelType) throws Exception;

    void updateTitle(UUID id, String newTitle) throws Exception;

    void updateDescription(UUID id, String newDescription) throws Exception;
}
