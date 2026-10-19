package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class Channel extends BaseEntity {
    private UUID ownerId;
    private String name;
    private String description;
    private final Set<UUID> users = new HashSet<>();
    private final List<UUID> messages = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public Channel(UUID ownerId, String name, String description) {
        super();
        this.ownerId = Objects.requireNonNull(ownerId, "소유자는 필수입니다.");

        validateName(name);
        this.name = name;
        this.description = description;
        users.add(ownerId); // 소유자는 자동으로 가입한다.
    }

    public UUID getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public Set<UUID> getUsers() { return Collections.unmodifiableSet(users); }
    public List<UUID> getMessages() { return Collections.unmodifiableList(messages); }

    public boolean isOwner(UUID userId) { return ownerId.equals(userId); }
    public boolean hasUser(UUID userId) { return users.contains(userId); }

    public void update(String name, String description) {
        validateName(name);
        this.name = name;
        this.description = description;
        super.touch();
    }

    public void addUser(UUID userId) {
        if (!users.add(userId)) {
            throw new IllegalArgumentException("이미 가입된 채널입니다.");
        }
        touch();
    }

    public void removeUser(UUID userId) {
        if (isOwner(userId)) {
            throw new IllegalArgumentException("채널 소유주는 퇴장이 불가합니다.");
        }
        if (!users.remove(userId)) {
            throw new IllegalArgumentException("채널 멤버가 아닙니다.");
        }
        touch();
    }

    public void addMessage(UUID messageId) {
        messages.add(messageId);
        touch();
    }

    public void removeMessage(UUID messageId) {
        messages.remove(messageId);
        touch();
    }

    public void transferOwnership(UUID newOwnerId) {
        if (!hasUser(newOwnerId)) {
            throw new IllegalArgumentException("채널 멤버에게만 양도 가능합니다.");
        }
        this.ownerId = newOwnerId;
        touch();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널명은 필수입니다.");
        }
    }
}
