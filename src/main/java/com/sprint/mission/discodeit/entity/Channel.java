package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends Base{
    private String name;
    private final List<User> users;

    public Channel(String name, List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("채널에는 최소한 한 명이 있어야 합니다.");
        }
        this.name = name;
        this.users = new ArrayList<>(users);
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        boolean exists = users.stream()
                .anyMatch(existsUser -> existsUser.getId().equals(user.getId()));

        if (exists) {
            throw new IllegalArgumentException("이미 채널에 존재하는 유저입니다.");
        }

        this.users.add(user);
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void removeUser(User user) {
        boolean exists = users.stream()
                .anyMatch(existsUser -> existsUser.getId().equals(user.getId()));

        if (!exists) {
            throw new IllegalArgumentException("채널에 존재하지 않는 유저입니다.");
        }
        if (users.size() == 1) {
            throw new IllegalStateException("채널에 최소 한명의 유저는 존재하여야합니다.");
        }
        this.users.remove(user);
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
