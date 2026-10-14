package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private UUID ownerId;
    private String channelName;
    private String description;
    private List<UUID> messages;

    public Channel(UUID ownerId, String channelName, String description) {
        super();

        this.ownerId = ownerId;
        this.channelName = channelName;

        requiredValue(channelName);
        this.description = description;

        messages = new ArrayList<>();
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

    public boolean addMessage(UUID messageId) {
        return messages.add(messageId);
    }

    public boolean removeMessage(UUID messageID) {
        return messages.remove(messageID);
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
