package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity  {

    private String name;
    private String email;
    private List<Message> messages = new ArrayList<>();
    private List<Channel> channels = new ArrayList<>();

    public User(String name, String email) {
        super();

        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void update(String name, String email) {
        this.name = name;
        this.email = email;
        updateUpdatedAt();
    }
    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels);
    }

    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }

    public void addChannel(Channel channel) {
        if (!channels.contains(channel)) {
            channels.add(channel);
        }
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
    }

    public boolean hasChannel(Channel channel) {
        return channels.contains(channel);
    }
}
