package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {

    private String name;

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
        updateTimestamp();
    }
}