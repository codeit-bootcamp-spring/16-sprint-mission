package com.sprint.mission.discodeit.entity;

import java.util.*;
import java.util.stream.Collectors;

public class Channel extends BaseEntity {
    private Set<UUID> users;
    private Set<UUID> messages;
    private String name;

    public Channel(String name) {
        super();
        this.name = name;
        this.users = new HashSet<>();
        this.messages = new LinkedHashSet<>();
    }

    public Channel update(Channel channel) {
        this.name = channel.name;
        this.users = channel.getUsers();
        this.messages = channel.getMessages();
        super.update();
        return this;
    }

    public void validateNotDeleted() {
        if (this.isDeleted()) throw new IllegalStateException("삭제된 채널입니다.");
    }

    public void addUser(UUID userId) {
        this.users.add(userId);
        super.update();
    }

    public void removeUser(UUID userId) {
        this.users.remove(userId);
        super.update();
    }

    public void addMessage(UUID messageId) {
        this.messages.add(messageId);
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

    public Set<UUID> getUsers() {
        return this.users;
    }

    public Set<UUID> getMessages() {
        return this.messages;
    }

    @Override
    public String toString() {
        String userStr = this.users.stream().map(uuid -> uuid.toString().substring(0, 4)).collect(Collectors.joining(
                ", "));
        String msgStr =
                this.getMessages().stream().map(uuid -> uuid.toString().substring(0, 4)).collect(Collectors.joining(
                        ", "));
        return "이름: %s, id: %s, 가입 유저 (id): [ %s ], 메세지 내역 (id): [ %s ], 상태: %s".formatted(
                this.name,
                this.getId().toString().substring(0, 4), userStr,
                msgStr,
                super.getStatus());
    }
}
