package com.sprint.mission.discodeit.entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class User extends BaseEntity {
    private final Map<UUID, Channel> channels;
    private final Map<UUID, Message> messages;
    private String name;

    public User(String name) {
        super();
        this.name = name;
        this.channels = new HashMap<>();
        this.messages = new LinkedHashMap<>();
    }

    public User updateName(String name) {
        this.name = name;
        super.update();
        return this;
    }

    public void addChannel(Channel channel) {
        this.channels.put(channel.getId(), channel);
        super.update();
    }

    public void removeChannel(UUID channelId) {
        this.channels.remove(channelId);
        super.update();
    }

    public void addMessage(Message message) {
        this.messages.put(message.getId(), message);
        super.update();
    }

    public void removeMessage(UUID messageId) {
        this.messages.remove(messageId);
        super.update();
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
        return "이름: %s, 가입 채널: [ %s ], 상태: %s".formatted(this.name, str, super.getStatus());
    }
}
