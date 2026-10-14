package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends Entity {

    private String title;
    private String description;
    private ChannelType channelType;

    private final List<UUID> userUUIDs;
    private final List<Message> channelMessages;

    public Channel(ChannelType channelType, String title, String description) {
        this.userUUIDs = new ArrayList<>();
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

    public List<UUID> getUserUUIDs() {
        return userUUIDs;
    }

    public void addUser(User user) {
        userUUIDs.add(user.getId());
    }

    public void deleteUser(User user) {
        userUUIDs.remove(user.getId());
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
