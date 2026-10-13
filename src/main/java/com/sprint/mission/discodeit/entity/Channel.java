package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends Base{
    private String name;
//    private List<>UUID> userId;

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
        updateUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
//                ", userId=" + userId +
                '}';
    }
}
