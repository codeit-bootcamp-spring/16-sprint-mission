package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public enum ChannelType {
    PUBLIC("공개"),
    PRIVATE("비공개");
    private final String value;

    ChannelType(String value) {
        this.value = value;
    }
}
