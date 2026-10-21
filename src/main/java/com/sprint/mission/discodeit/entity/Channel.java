package com.sprint.mission.discodeit.entity;

import java.util.*;
import java.util.stream.Collectors;

public class Channel extends BaseEntity {
    private Map<UUID, User> users;
    private Map<UUID, Message> messages;
    private String name;

    public Channel(String name) {
        super();
        this.name = name;
        this.users = new HashMap<>();
        this.messages = new LinkedHashMap<>();
    }

    public Channel update(Channel channel) {
        this.name = channel.name;
        this.users = channel.users;
        this.messages = channel.messages;
        super.update();
        return this;
    }

    public void addUser(User user) {
        this.users.put(user.getId(), user);
        super.update();
    }

    public void removeUser(UUID userId) {
        this.users.remove(userId);
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

    public void setName(String name) {
        this.name = name;
    }

    public Map<UUID, User> getUsers() {
        return this.users;
    }

    public Map<UUID, Message> getMessages() {
        return this.messages;
    }

    @Override
    public String toString() {
        String userStr = this.users.values()
                .stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));
        String msgStr =
                this.getMessages().values().stream().map(Message::getContent).collect(Collectors.joining(", "));
        return "이름: %s, 가입 유저: [ %s ], 메세지 내역: [ %s ], 상태: %s".formatted(this.name, userStr, msgStr, super.getStatus());
    }
}
