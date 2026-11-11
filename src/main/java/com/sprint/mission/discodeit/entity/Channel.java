package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Channel extends BaseEntity implements Updatable {
    private final Set<UUID> users;
    private final ChannelType type;

    private Instant updatedAt;
    private String name;
    private String description;

    public Channel(String name, String description, ChannelType type) {
        super();
        this.updatedAt = this.getCreatedAt();
        this.name = name;
        this.description = description;
        this.type = type;
        this.users = new HashSet<>();
    }

    public Channel(List<UUID> memberIds, ChannelType type) {
        super();
        if (type != ChannelType.PRIVATE) throw new IllegalArgumentException("비공개 채널만 이름과 설명을 생략할 수 있습니다.");
        this.updatedAt = this.getCreatedAt();
        this.type = type;
        this.users = new HashSet<>(memberIds);
    }

    public Channel update(String newName, String newDescription) {
        boolean updated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            updated = true;
        }

        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            updated = true;
        }

        if (updated) {
            this.update();
        }

        return this;
    }

    @Override
    public void update() {
        this.updatedAt = Instant.now();
    }

    public void addUser(UUID userId) {
        this.users.add(userId);
        this.update();
    }

    public void removeUser(UUID userId) {
        this.users.remove(userId);
        this.update();
    }

    @Override
    public String toString() {
        String userStr = this.users
                .stream()
                .map(uuid -> uuid.toString().substring(0, 4))
                .collect(Collectors.joining(", "));
        return "이름: %s, id: %s, 가입 유저 (id): [ %s ]".formatted(
                this.name,
                this.getId().toString().substring(0, 4), userStr);
    }
}
