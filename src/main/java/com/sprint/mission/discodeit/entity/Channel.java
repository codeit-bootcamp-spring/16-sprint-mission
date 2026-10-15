package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;
    private final List<User> channelParticipants;
    private final List<Message> channelMessages;
    private final UUID creatorId;

    public Channel(String channelName, UUID creatorId) {
        super();

        Validation.validateBlank(channelName, "channelName");
        Validation.validateWhitespace(creatorId, "creatorId");

        this.channelName = channelName;
        this.channelParticipants = new ArrayList<>();
        this.channelMessages = new ArrayList<>();
        this.creatorId = creatorId;
    }

    public String getChannelName() { return channelName; }

    public UUID getCreatorId() { return creatorId; }

    public List<User> getChannelParticipants() { return channelParticipants; }

    public List<Message> getChannelMessages() { return channelMessages; }

    public void changeChannelName(String channelName) {
        Validation.validateBlank(channelName, "channelName");

        this.channelName = channelName;
        super.update();
    }

    public void joinChannel(User user) {
        Validation.validateNull(user, "user");
        if (channelParticipants.contains(user)) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 유저입니다.");
        }

        channelParticipants.add(user);
        user.addChannel(this);

        Message message = new Message(
                user.getUsername() + "님이 참여하셨습니다.", user.getId(), this.getId());

        addChannelMessages(message);
    }

    public void leaveChannel(User user) {
        Validation.validateNull(user, "user");
        if (!channelParticipants.contains(user)) {
            throw new IllegalArgumentException("해당 유저는 채널에 참여하고 있지 않습니다.");
        }

        channelParticipants.remove(user);
        user.removeChannel(this);

        Message message = new Message(
                user.getUsername() + "님이 퇴장하셨습니다.", user.getId(), this.getId());

        addChannelMessages(message);
    }

    public void addChannelMessages(Message message) {
        channelMessages.add(message);
    }

    public void removeChannelMessages(Message message) {
        channelMessages.remove(message);
    }

    @Override
    public String toString() {
        return "채널 \"" + channelName + "\" 정보: {" +
                "채널 이름='" + channelName + '\'' +
                ", 채널 방장=" + creatorId +
                '}';
    }
}
