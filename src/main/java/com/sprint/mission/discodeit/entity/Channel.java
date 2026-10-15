package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Channel extends Entity implements Serializable {

    private String title;
    private String description;
    private ChannelType channelType;

    private static final long serialVersionUID = 3;
    private final List<User> users;
    private final List<Message> channelMessages;

    public Channel(ChannelType channelType, String title, String description) {
        this.users = new ArrayList<>();
        this.channelMessages = new ArrayList<>();
        this.channelType = channelType;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    // null이 아닌 값만 수정하고 수정 시간을 갱신한다.
    public void update(ChannelType channelType, String title, String description) {
        if (channelType != null) {
            this.channelType = channelType;
        }
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        touch();
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void deleteUser(User user) {
        users.remove(user);
    }

    public List<Message> getChannelMessages() {
        return channelMessages;
    }

    public void addMessage(Message message) {
        channelMessages.add(message);
    }

    public void deleteMessage(Message message) {
        channelMessages.remove(message);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", channelType=" + channelType +
                ", id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}