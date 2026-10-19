package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class User extends BaseEntity {
    private String username;
    private String email;
    private transient String password;
    private final Set<UUID> channels = new HashSet<>();
    private final List<UUID> messages = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public User(String username, String email, String password) {
        super();
        validate(username, email, password);
        this.username = username;
        this.email = email;
        this.password = password;

    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public Set<UUID> getChannels() { return Collections.unmodifiableSet(channels); }
    public List<UUID> getMessages() { return Collections.unmodifiableList(messages); }

    public boolean hasChannel(UUID channelId) { return channels.contains(channelId); }

    public void update(String username, String email, String password) {
        validate(username, email, password);
        this.username = username;
        this.email = email;
        this.password = password;
        touch();
    }

    public void addChannel(UUID channelId) {
        if (!channels.add(channelId)) {
            throw new IllegalArgumentException("이미 가입된 채널입니다.");
        }
        touch();
    }

    public void removeChannel(UUID channelId) {
        if (!channels.remove(channelId)) {
            throw new IllegalArgumentException("가입하지 않은 채널입니다.");
        }
        touch();
    }

    public void addMessage(UUID messageId) {
        messages.add(messageId);
        touch();
    }

    public void removeMessage(UUID messageId) {
        messages.remove(messageId);
        touch();
    }

    private static void validate(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
    }
}
