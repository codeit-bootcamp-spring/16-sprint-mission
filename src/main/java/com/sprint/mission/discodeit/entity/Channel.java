package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    private String channelname;
    private String description;

    public Channel(String channelname, String description) {
        super();
        this.channelname = channelname;
        this.description = description;

    }
}
