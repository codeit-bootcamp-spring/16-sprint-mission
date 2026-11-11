package com.sprint.mission.discodeit.entity;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class Channel extends BaseEntity{
    private static final long serialVersionUID = 1L;

    @Getter(AccessLevel.NONE)
    private final ChannelType type;
    private String name;
    private String description;

    /** 기존 코드와의 호환을 위해 타입을 생략하면 공개 채널로 만듭니다. */
    public Channel(String name, String description) {
        this(ChannelType.PUBLIC, name, description);
    }

    public Channel(ChannelType type, String name, String description) {
        super();
        if (type == null) {
            throw new IllegalArgumentException("Channel type cannot be null.");
        }
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        updateUpdatedAt();
    }

    public ChannelType getType() {
        return type == null ? ChannelType.PUBLIC : type;
    }
}
