package com.sprint.mission.discodeit.entity;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;

    private final Set<UUID> channelIds;
    private final Set<UUID> messageIds;

    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.channelIds = new LinkedHashSet<>();
        this.messageIds = new LinkedHashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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