package com.sprint.mission.entity;

public class Channel extends Entity {

    private String title;
    private String description;
    private ChannelType channelType;

    public Channel(String createdAt, String updatedAt, String title, String description, ChannelType channelType) {
        super(createdAt, updatedAt);
        this.title = title;
        this.description = description;
        this.channelType = channelType;
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
