package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Channel extends Base {
    private ChannelType type;
    private String name;
    private String description;

    public void update(String name, String description) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        updateUpdatedAt();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "description='" + description + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
