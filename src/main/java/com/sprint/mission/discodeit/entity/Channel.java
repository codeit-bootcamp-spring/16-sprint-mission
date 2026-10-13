package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;
    private String description;

    public Channel(String channelName, String description) {
        super();
        this.channelName = channelName;
        this.description = description;
    }

    public String getChannelName() {
        return channelName;
    }

    private void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        super.update();
    }

    public void updateDescription(String description) {
        this.description = description;
        super.update();
    }
}
