package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends Base{
    private String name;
    private final List<User> users;
    private final List<Message> messages;

    public Channel(String name, List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("채널에는 최소 1명의 사용자가 존재해야 합니다.");
        }
        this.name = name;
        this.users = new ArrayList<>(users);
        this.messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return List.copyOf(users);
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        boolean exists = users.stream()
                .anyMatch(existsUser -> existsUser.getId().equals(user.getId()));

        if (exists) {
            throw new IllegalArgumentException("이미 채널에 존재하는 유저입니다.");
        }
        this.users.add(user);
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void removeUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }
        boolean exists = users.stream()
                .anyMatch(existsUser -> existsUser.getId().equals(user.getId()));

        if (!exists) {
            throw new IllegalArgumentException("채널에 존재하지 않는 유저입니다.");
        }
        if (users.size() == 1) {
            throw new IllegalStateException("채널에 최소 한명의 유저는 존재하여야합니다.");
        }
        users.removeIf(existsUser ->
                existsUser.getId().equals(user.getId()));

        updateUpdatedAt(System.currentTimeMillis());
    }

    public void addMessage(Message message) {
        messages.add(message);
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void removeMessage(Message message) {
        messages.removeIf(existsMessage ->
                existsMessage.getId().equals(message.getId()));
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void update(String name) {
        this.name = name;
        updateUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
//                ", userId=" + userId +
                '}';
    }
}
