package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ChannelType;

public interface ChannelUpdate {

    void updateChannelType(String id, ChannelType newChannelType) throws Exception;

    void updateTitle(String id, String newTitle) throws Exception;

    void updateDescription(String id, String newDescription) throws Exception;
}
