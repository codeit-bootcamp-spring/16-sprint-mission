package com.sprint.mission.discodeit.entity;

import java.util.*;
import java.util.stream.Collectors;

public class User extends BaseEntity {
    private Set<UUID> channels;
    private Set<UUID> messages;
    private String name;

    public User(String name) {
        super();
        this.name = name;
        this.channels = new HashSet<>();
        this.messages = new LinkedHashSet<>();
    }

    public User update(User user) {
        this.name = user.getName();
        this.channels = user.getChannels();
        this.messages = user.getMessages();
        super.update();
        return this;
    }

    public void validateNotDeleted() {
        if (this.isDeleted()) {
            throw new IllegalStateException("삭제된 유저입니다.");
        }
    }

    public void addChannel(UUID channelId) {
        this.channels.add(channelId);
        super.update();
    }

    public void removeChannel(UUID channelId) {
        this.channels.remove(channelId);
        super.update();
    }

    //
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

    public Set<UUID> getChannels() {
        return this.channels;
    }

    public Set<UUID> getMessages() {
        return this.messages;
    }

    @Override
    public String toString() {
        String str =
                this.channels.stream().map(uuid -> uuid.toString().substring(0, 4)).collect(Collectors.joining(", "));
        return "이름: %s, id: %s, 가입 채널: [ %s ], 상태: %s".formatted(
                this.name,
                this.getId().toString().substring(0, 4),
                str, super.getStatus());
    }
}
