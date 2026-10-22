package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Base{
    private String name;
    private int age;
    private final List<Channel> channels;
    private final List<Message> messages;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels);
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public void addChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        boolean exists = channels.stream()
                        .anyMatch(existsChannel -> existsChannel.getId().equals(channel.getId()));

        if (exists) {
            throw new IllegalArgumentException("이미 참여하고 있는 채널입니다.");
        }
        channels.add(channel);
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void removeChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }
        boolean exists = channels.stream()
                        .anyMatch(existsChannel -> existsChannel.getId().equals(channel.getId()));
        if (!exists) {
            throw new IllegalArgumentException("참여하고 있지 않은 채널입니다.");
        }
        channels.removeIf(existsChannel ->
                existsChannel.getId().equals(channel.getId()));
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
