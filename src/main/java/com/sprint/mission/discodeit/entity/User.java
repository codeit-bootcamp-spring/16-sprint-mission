package com.sprint.mission.discodeit.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class User extends BaseEntity {
    private String name;
    private Map<UUID, Channel> channels;

    public User(String name) {
        super();
        this.name = name;
        this.channels = new HashMap<>();
    }

    public User update(String name) {
        super.update();
        this.name = name;
        return this;
    }

    public User update(Map<UUID, Channel> channels) {
        super.update();
        this.channels = new HashMap<>(channels);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Map<UUID, Channel> getChannels() {
        return new HashMap<>(this.channels);
    }

    @Override
    public String toString() {
        String str =
                this.channels.values().stream().map(Channel::getName).collect(Collectors.joining(", "));
        return "이름: %s, 가입 채널: [ %s ]".formatted(this.name, str);
    }
}
