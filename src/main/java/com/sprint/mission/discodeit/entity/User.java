package com.sprint.mission.discodeit.entity;

import java.util.List;

public class User extends BaseEntity {
    private String name;
    private List<Channel> channels;

    public User(String name) {
        super();
        this.name = name;
    }

    public User update(String name) {
        super.update();
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }
}
