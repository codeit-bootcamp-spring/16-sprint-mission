package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private Set<UUID> channels;
    private List<UUID> messages;

    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;

        channels = new HashSet<>();
        messages = new ArrayList<>();

        requiredValue(username, email, password);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<UUID> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    public List<UUID> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void updateUsername(String username) {
        requiredUsername(username);
        this.username = username;
        super.update();
    }

    public void updateEmail(String email) {
        requiredEmail(email);
        this.email = email;
        super.update();
    }

    public void updatePassword(String password) {
        requiredPassword(password);
        this.password = password;
        super.update();
    }

    public boolean addMessage(UUID messageId) {
        return messages.add(messageId);
    }

    public boolean removeMessage(UUID messageId) {
        return messages.remove(messageId);
    }

    public boolean addChannel(UUID channelId) {
        return channels.add(channelId);
    }

    public boolean removeChannel(UUID channelId) {
        return channels.remove(channelId);
    }

    public boolean hasChannel(UUID channelId) {
        return channels.contains(channelId);
    }

    private static void requiredValue(String username, String email, String password) {
        requiredUsername(username);
        requiredEmail(email);
        requiredPassword(password);
    }

    private static void requiredPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }

    private static void requiredEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
    }

    private static void requiredUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
    }
}
