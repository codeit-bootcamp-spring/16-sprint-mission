package com.sprint.mission.discodeit.entity;

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

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
