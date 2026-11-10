
package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class Channel extends BaseEntity {

    private ChannelType type;
    private String name;
    private String description;

    public Channel(String name) {
        this(ChannelType.PUBLIC, name, null);
    }

    public Channel(ChannelType type, String name, String description) {
        if (type == null) {
            throw new IllegalArgumentException("채널 종류가 필요합니다.");
        }

        if (type == ChannelType.PUBLIC && (name == null || name.isBlank())) {
            throw new IllegalArgumentException("공개 채널 이름이 필요합니다.");
        }

        if (type == ChannelType.PRIVATE && (name != null || description != null)) {
            throw new IllegalArgumentException("비공개 채널에는 이름과 설명을 지정하지 않습니다.");
        }

        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String name) {
        update(name, this.description);
    }

    public void update(String name, String description) {
        if (type == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름이 필요합니다.");
        }

        this.name = name;
        this.description = description;
        updateTimestamp();
    }
}
