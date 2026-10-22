package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String channelName;
    private final List<User> channelParticipants;
    private final List<Message> channelMessages;
    private final User creator;

    public Channel(String channelName, User creator) {
        super();
        Validation.validateBlank(channelName, "channelName");
        Validation.validateNull(creator, "creator");
        this.channelName = channelName;
        this.channelParticipants = new ArrayList<>();
        this.channelMessages = new ArrayList<>();
        this.creator = creator;
    }

    public String getChannelName() { return channelName; }
    public List<User> getChannelParticipants() { return new ArrayList<>(channelParticipants); }
    public List<Message> getChannelMessages() { return new ArrayList<>(channelMessages); }
    public User getCreator() { return creator; }

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
        super.update();
    }

    public void leaveChannel(User user) {
        Validation.validateNull(user, "user");
        if (!channelParticipants.contains(user)) {
            throw new IllegalArgumentException("해당 유저는 채널에 참여하고 있지 않습니다.");
        }
        channelParticipants.remove(user);
        user.removeChannel(this);
        super.update();
    }

    public void addChannelMessages(Message message) {
        Validation.validateNull(message, "message");
        channelMessages.add(message);
        super.update();
    }

    public void removeChannelMessages(Message message) {
        channelMessages.remove(message);
        super.update();
    }
}
