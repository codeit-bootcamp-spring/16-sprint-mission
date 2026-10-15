package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private final List<Channel> myChannels;
    private final List<Message> myMessages;

    public User(String username, String password, String email) {
        super();

        Validation.validateWhitespace(username, "username");
        Validation.validateWhitespace(password, "password");
        if (email != null) {
            Validation.validateWhitespace(email, "email");
            Validation.validateEmailRegex(email);
        }

        this.username = username;
        this.password = password;
        this.email = email;
        this.myChannels = new ArrayList<>();
        this.myMessages = new ArrayList<>();
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getEmail() { return email; }

    public List<Channel> getMyChannels() { return new ArrayList<>(myChannels); }

    public List<Message> getMyMessages() { return new ArrayList<>(myMessages); }

    public void changeUsername(String username) {
        Validation.validateWhitespace(username, "username");

        this.username = username;
        super.update();
    }

    public void changePassword(String password) {
        Validation.validateWhitespace(password, "password");

        this.password = password;
        super.update();
    }

    public void changeEmail(String email) {
        Validation.validateWhitespace(email, "email");
        Validation.validateEmailRegex(email);

        this.email = email;
        super.update();
    }

    public void addChannel(Channel channel) {
        Validation.validateNull(channel, "channel");
        if (myChannels.contains(channel)) {
            throw new IllegalArgumentException("이미 참여 중인 채널입니다.");
        }

        myChannels.add(channel);
    }

    public void addMessage(Message message) {
        Validation.validateNull(message, "message");

        myMessages.add(message);
    }

    public void removeChannel(Channel channel) {
        Validation.validateNull(channel, "channel");
        if (!myChannels.contains(channel)) {
            throw new IllegalArgumentException("해당 채널에 참여하고 있지 않습니다.");
        }

        myChannels.remove(channel);
    }

    public void removeMessage(Message message) {
        Validation.validateNull(message, "message");

        myMessages.remove(message);
    }

    public void deleteEmail() {
        this.email = null;
        super.update();
    }

    @Override
    public String toString() {
        return username + " 정보: " +
                "{이름='" + username + '\'' +
                ", 이메일='" + email + '\'' +
                '}';
    }
}