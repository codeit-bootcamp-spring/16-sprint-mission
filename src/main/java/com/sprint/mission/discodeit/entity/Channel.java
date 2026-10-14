package com.sprint.mission.discodeit.entity;

import java.util.*;
import java.util.stream.Collectors;

public class Channel extends BaseEntity {
    private String name;
    private Map<UUID, User> users;
    private List<Message> messages;

    public Channel(String name) {
        super();
        this.name = name;
        this.users = new HashMap<>();
        this.messages = new ArrayList<>();
    }

    public Channel updateName(String name) {
        super.update();
        this.name = name;
        return this;
    }

    public Channel updateUsers(Map<UUID, User> users) {
        super.update();
        this.users = new HashMap<>(users);
        return this;
    }

    public Channel updateMessages(List<Message> messages) {
        super.update();
        this.messages = new ArrayList<>(messages);
        return this;
    }

    public void addUser(User user) {
        this.users.put(user.getId(), user);
        this.updateUsers(this.users);
    }

    public void removeUser(UUID userId) {
        this.users.remove(userId);
        this.updateUsers(this.users);
    }

    public String getName() {
        return this.name;
    }

    public Map<UUID, User> getUsers() {
        return new HashMap<>(this.users);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(this.messages);
    }

    @Override
    public String toString() {
        String userStr = this.users.values()
                .stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));
        String msgStr =
                this.messages.stream().map(Message::getContent).collect(Collectors.joining("\n,"));
        return "이름: %s, 가입 유저: [ %s ], 메세지 내역: [ %s ]".formatted(this.name, userStr, msgStr);
    }
}
