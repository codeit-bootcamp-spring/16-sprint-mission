package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private ChannelType type;
    private String name;
    private String description;

    // 공개 채널 생성자
    public Channel(String name, String description) {
        this(ChannelType.PUBLIC, name, description);
    }

    // 비공개 채널 생성자
    public Channel() {
        this(ChannelType.PRIVATE, null, null);
    }

    // 공통 생성자
    private Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.type = type;
        this.name = name;
        this.description = description;
    }

    // 채널 정보 수정
    public void update(String newName, String newDescription) {
        if (type == ChannelType.PRIVATE) {
            throw new IllegalStateException("비공개 채널은 수정할 수 없습니다.");
        }

        boolean anyValueUpdated = false;

        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }

        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}

