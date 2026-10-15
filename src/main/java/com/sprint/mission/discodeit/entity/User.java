package com.sprint.mission.discodeit.entity;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class User extends BaseEntity {
    private String name;
    private Map<UUID, Channel> channels;
    private Map<UUID, Message> messages;

    public User(String name, Map<UUID, Channel> channels, Map<UUID, Message> messages) {
        super();
        this.name = name;
        this.channels = channels;
        this.messages = messages;
    }

    public User updateName(String name) {
        super.update();
        this.name = name;
        return this;
    }

    public User updateChannels(Map<UUID, Channel> channels) {
        super.update();
        this.channels = channels;
        return this;
    }

    public User updateMessages(Map<UUID, Message> messages) {
        super.update();
        this.messages = messages;
        return this;
    }

    public void addChannel(Channel channel) {
        this.channels.put(channel.getId(), channel);
        this.updateChannels(this.channels);
    }

    public void removeChannel(UUID channelId) {
        this.channels.remove(channelId);
        this.updateChannels(this.channels);
    }

    public void removeMessage(UUID messageId) {
        this.messages.remove(messageId);
        this.updateMessages(this.messages);
    }

    public String getName() {
        return this.name;
    }

    public Map<UUID, Channel> getChannels() {
        return this.channels;
    }

    public Map<UUID, Message> getMessages() {
        return this.messages;
    }

    @Override
    public String toString() {
        String str =
                this.channels.values().stream().map(Channel::getName).collect(Collectors.joining(", "));
        return "이름: %s, 가입 채널: [ %s ]".formatted(this.name, str);
    }
}
