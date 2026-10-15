package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {

    private String channelName;

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void updateChannelName(String channelName) {
        this.update();
        this.channelName = channelName;
    }
}
