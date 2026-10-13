package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;
    private final UUID creatorId;

    public Channel(String channelName, UUID creatorId) {
        super();

        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널명을 작성해 주세요.");
        }

        Validation.validateBlank(creatorId, "creatorId");

        this.channelName = channelName;
        this.creatorId = creatorId;
    }

    public String getChannelName() {
        return channelName;
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public void changeChannelName(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널명을 작성해 주세요.");
        }

        this.channelName = channelName;
        super.update();
    }
}
