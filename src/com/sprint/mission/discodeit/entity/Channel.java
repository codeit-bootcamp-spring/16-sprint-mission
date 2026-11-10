package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class Channel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private ChannelType type;

    public Channel(String name, String description) {
        this(name, description, ChannelType.PUBLIC);
    }

    public Channel(String name, String description, ChannelType type) {
        super();

        if (type == null) {
            throw new IllegalArgumentException("채널 유형은 필수입니다.");
        }

        if (type == ChannelType.PUBLIC) {
            validateRequired(name, "채널 이름");
        }

        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void update(String name, String description) {
        if (type == ChannelType.PRIVATE) {
            throw new IllegalStateException("비공개 채널은 수정할 수 없습니다.");
        }

        validateRequired(name, "채널 이름");

        this.name = name;
        this.description = description;
        updateUpdatedAt();
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은(는) 필수입니다."
            );
        }
    }
}