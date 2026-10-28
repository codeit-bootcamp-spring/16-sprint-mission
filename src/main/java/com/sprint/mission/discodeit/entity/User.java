package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 2L;

    private String username;
    private String password;
    private String email;
    private final List<UUID> myChannelIds;
    private final List<UUID> myMessageIds;

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
        this.myChannelIds = new ArrayList<>();
        this.myMessageIds = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<UUID> getMyChannelIds() {
        return new ArrayList<>(myChannelIds);
    }

    public List<UUID> getMyMessageIds() {
        return new ArrayList<>(myMessageIds);
    }

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

    public void addChannelId(UUID channelId) {
        Validation.validateNull(channelId, "channelId");
        if (myChannelIds.contains(channelId)) {
            throw new IllegalArgumentException("이미 참여 중인 채널입니다.");
        }
        myChannelIds.add(channelId);
        super.update();
    }

    public void addMessageId(UUID messageId) {
        Validation.validateNull(messageId, "messageId");
        if (myMessageIds.contains(messageId)) {
            throw new IllegalArgumentException("이미 등록된 메시지입니다.");
        }
        myMessageIds.add(messageId);
        super.update();
    }

    public void removeChannelId(UUID channelId) {
        Validation.validateNull(channelId, "channelId");
        if (!myChannelIds.remove(channelId)) {
            throw new IllegalArgumentException("해당 채널에 참여하고 있지 않습니다.");
        }
        super.update();
    }

    public void removeMessageId(UUID messageId) {
        Validation.validateNull(messageId, "messageId");
        myMessageIds.remove(messageId);
        super.update();
    }

    public void deleteEmail() {
        this.email = null;
        super.update();
    }
}
