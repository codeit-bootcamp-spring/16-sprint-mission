package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    // final 인스턴스 필드는 객체(인스턴스) 생성시점(생성자 호출시)에 반드시 초기화되어야합니다.
    // 그렇지 않다면 컴파일 오류가 발생합니다.
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private final ChannelType type;
    private String name;
    private String description;

    /*
    public Channel(PublicChannelCreateRequest request) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.type = ChannelType.PUBLIC;
        this.name = request.name();
        this.description = request.description();
    }

    public Channel(PrivateChannelCreateRequest request) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.type = ChannelType.PRIVATE;
    }
     */

    public Channel(ChannelCreateRequest request) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.type = request.type();
        this.name = request.name();
        this.description = request.description();
    }

    /*
    public Channel(ChannelCreateRequest request) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.type = ChannelType.PRIVATE;
    }
     */

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.isBlank() && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }

        // null을 전달해서 기존 description을 삭제하는 구조로 작성하지는 않았습니다.
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
