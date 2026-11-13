package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2L;

    private String name;
    private String description;
    private final ChannelType type;
    private final List<UUID> participantIds;
    private final List<UUID> messageIds;

    public Channel(
            ChannelType type,
            String name,
            String description
    ) {
        super();

        Validation.validateNull(type, "type");

        if (type == ChannelType.PUBLIC) {
            Validation.validateBlank(name, "name");
        } else {
            name = null;
            description = null;
        }

        this.type = type;
        this.name = name;
        this.description = description;
        this.participantIds = new ArrayList<>();
        this.messageIds = new ArrayList<>();
    }

    public List<UUID> getParticipantIds() {
        return new ArrayList<>(participantIds);
    }

    public List<UUID> getMessageIds() {
        return new ArrayList<>(messageIds);
    }

    public void changeName(String name) {
        if (type == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        Validation.validateBlank(name, "name");

        this.name = name;

        super.update();
    }

    public void changeDescription(String description) {
        if (type == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        this.description = description;

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

        if (messageIds.remove(messageId)) {
            super.update();
        }
    }
}
