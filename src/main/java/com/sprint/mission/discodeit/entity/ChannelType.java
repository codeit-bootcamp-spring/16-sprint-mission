package com.sprint.mission.discodeit.entity;

public enum ChannelType {
    PUBLIC("공개"),
    PRIVATE("비공개");
    private final String value;

    ChannelType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
