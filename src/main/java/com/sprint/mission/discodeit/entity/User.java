package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Base{
    private String name;
    private int age;
    private final List<Channel> channels = new ArrayList<>();

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void addChannel(Channel channel) {
        boolean exists = channels.stream()
                        .anyMatch(existsChannel -> existsChannel.getId().equals(channel.getId()));

        if (exists) {
            throw new IllegalArgumentException("이미 참여하고 있는 채널입니다.");
        }
        channels.add(channel);
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void removeChannel(Channel channel) {
        boolean exists = channels.stream()
                        .anyMatch(existsChannel -> existsChannel.getId().equals(channel.getId()));
        if (!exists) {
            throw new IllegalArgumentException("참여하고 있지 않은 채널입니다.");
        }
        channels.remove(channel);
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void update(String name, int age) {
        this.name = name;
        this.age = age;
        updateUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "User {" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
