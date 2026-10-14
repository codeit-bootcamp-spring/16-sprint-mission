package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends BaseEntity {
    private UUID ownerId;
    private String channelName;
    private String description;

    public Channel(UUID ownerId, String channelName, String description) {
        super();

        this.ownerId = ownerId;
        this.channelName = channelName;
        requiredValue(channelName);
        this.description = description;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    private void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getChannelName() {
        return channelName;
    }

    private void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        super.update();
    }

    public void updateDescription(String description) {
        this.description = description;
        super.update();
    }

    private static void requiredValue(String channelName) {
        if (channelName.isBlank() || channelName == null) {
            throw new IllegalArgumentException("채널명은 필수입니다.");
        }
    }
}
