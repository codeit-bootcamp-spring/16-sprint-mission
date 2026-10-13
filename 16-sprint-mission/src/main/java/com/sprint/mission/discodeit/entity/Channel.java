package com.sprint.mission.discodeit.entity;

public class Channel extends Entity {

    private String title;
    private String description;
    private ChannelType channelType;

    public Channel(ChannelType channelType, String title, String description) {
        this.channelType = channelType;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }
}
