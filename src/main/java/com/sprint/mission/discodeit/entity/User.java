package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class User extends BaseEntity {

    private String username;
    private String email;
    private String password;
    private UUID profileId;

    private final Set<UUID> channelIds = new LinkedHashSet<>();
    private final Set<UUID> messageIds = new LinkedHashSet<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Set<UUID> getChannelIds() {
        return Collections.unmodifiableSet(channelIds);
    }

    public Set<UUID> getMessageIds() {
        return Collections.unmodifiableSet(messageIds);
    }

    public void update(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        updateTimestamp();
    }

    public void changeProfile(UUID profileId) {
        this.profileId = profileId;
        updateTimestamp();
    }

    public void addChannel(UUID channelId) {
        if (channelIds.add(channelId)) {
            updateTimestamp();
        }
    }

    public void removeChannel(UUID channelId) {
        if (channelIds.remove(channelId)) {
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