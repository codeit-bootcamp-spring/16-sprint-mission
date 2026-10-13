package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    private String channelName;

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            return;
        }
        this.channelName = channelName;
        super.update();
    }
}
