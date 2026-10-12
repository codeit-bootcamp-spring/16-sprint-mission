package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class User extends BaseEntity {
    private String name;
    private List<Channel> channels;

    public User(String name) {
        super();
        this.name = name;
        this.channels = new ArrayList<>();
    }

    public User update(String name) {
        super.update();
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        String str =
                this.channels.stream().map(Channel::getName).collect(Collectors.joining(", "));
        return "이름: %s, 가입 채널: [ %s ]".formatted(this.name, str);
    }
}
