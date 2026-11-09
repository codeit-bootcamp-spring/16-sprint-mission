package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {

    private final ChannelType type;
    private String name;
    private String description;

    private final Set<UUID> memberIds = new LinkedHashSet<>();
    private final Set<UUID> messageIds = new LinkedHashSet<>();

    public Channel(String name, String description) {
        this(ChannelType.PUBLIC, name, description);
    }

    public Channel(ChannelType type, String name, String description) {
        this.type = Objects.requireNonNull(type);
        this.name = name;
        this.description = description;
    }

    public Set<UUID> getMemberIds() {
        return Collections.unmodifiableSet(memberIds);
    }

    public Set<UUID> getMessageIds() {
        return Collections.unmodifiableSet(messageIds);
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        updateTimestamp();
    }

    public void addMember(UUID userId) {
        if (memberIds.add(userId)) {
            updateTimestamp();
        }
    }

    public void removeMember(UUID userId) {
        if (memberIds.remove(userId)) {
            updateTimestamp();
        }
    }

    public void addMessage(UUID messageId) {
        if (messageIds.add(messageId)) {
            updateTimestamp();
        }
    }

    public void removeMessage(UUID messageId) {
        if (messageIds.remove(messageId)) {
            updateTimestamp();
        }
    }
}