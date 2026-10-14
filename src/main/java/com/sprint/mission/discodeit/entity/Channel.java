package com.sprint.mission.discodeit.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {
    private UUID ownerId;
    private String channelName;
    private String description;
    private Set<UUID> members;

    public Channel(UUID ownerId, String channelName, String description) {
        super();

        this.ownerId = ownerId;
        this.channelName = channelName;

        requiredValue(channelName);
        this.description = description;

        members = new HashSet<>();
        members.add(ownerId);

    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }

    public void updateChannelName(String channelName) {
        requiredValue(channelName);
        this.channelName = channelName;
        super.update();
    }

    public void updateDescription(String description) {
        this.description = description;
        super.update();
    }

    public boolean addMember(UUID userId) {
        return members.add(userId);
    }

    public boolean removeMember(UUID userId) {
        return members.remove(userId);
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    private static void requiredValue(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널명은 필수입니다.");
        }
    }

    public void transferOwnership(UUID newOwnerId) {
        ownerId = newOwnerId;
    }
}
