package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class User extends Entity {

    private String userName;
    private String email;
    private String password;

    private final List<Channel> channels;
    private final List<Message> myMessages;

    public User(String userName, String email, String password) {
        this.channels = new ArrayList<>();
        this.myMessages = new ArrayList<>();
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMyMessages() {
        return myMessages;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void deleteChannel(Channel channel) {
        channels.remove(channel);
    }

    public void addMessage(Message message) {
        myMessages.add(message);
    }

    public void deleteMessage(Message message) {
        myMessages.remove(message);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
