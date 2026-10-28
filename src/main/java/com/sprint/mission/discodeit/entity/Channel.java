package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private String channelName;
    private final List<UUID> participantIds;
    private final List<UUID> messageIds;
    private final UUID creatorId;

    public Channel(String channelName, UUID creatorId) {
        super();
        Validation.validateBlank(channelName, "channelName");
        Validation.validateNull(creatorId, "creatorId");
        this.channelName = channelName;
        this.participantIds = new ArrayList<>();
        this.messageIds = new ArrayList<>();
        this.creatorId = creatorId;
    }

    public List<UUID> getParticipantIds() {
        return new ArrayList<>(participantIds);
    }

    public List<UUID> getMessageIds() {
        return new ArrayList<>(messageIds);
    }

    public void changeChannelName(String channelName) {
        Validation.validateBlank(channelName, "channelName");
        this.channelName = channelName;
        super.update();
    }

    public void joinChannel(UUID userId) {
        Validation.validateNull(userId, "userId");
        if (participantIds.contains(userId)) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 유저입니다.");
        }
        participantIds.add(userId);
        super.update();
    }

    public void leaveChannel(UUID userId) {
        Validation.validateNull(userId, "userId");
        if (!participantIds.remove(userId)) {
            throw new IllegalArgumentException("해당 유저는 채널에 참여하고 있지 않습니다.");
        }
        super.update();
    }

    public void addMessageId(UUID messageId) {
        Validation.validateNull(messageId, "messageId");
        if (messageIds.contains(messageId)) {
            throw new IllegalArgumentException("이미 등록된 메시지입니다.");
        }
        messageIds.add(messageId);
        super.update();
    }

    public void removeMessageId(UUID messageId) {
        Validation.validateNull(messageId, "messageId");
        messageIds.remove(messageId);
        super.update();
    }
}
