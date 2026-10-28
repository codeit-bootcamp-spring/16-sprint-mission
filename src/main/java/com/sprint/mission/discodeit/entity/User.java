package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends Entity implements Serializable {

    private String userName;
    private String email;

    private boolean isLogin;

    private String password;
    private static final long serialVersionUID = 1;

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

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // null이 아닌 값만 수정하고 수정 시간을 갱신한다.
    public void update(String userName, String email, String password) {
        if (userName != null) {
            this.userName = userName;
        }
        if (email != null) {
            this.email = email;
        }
        if (password != null) {
            this.password = password;
        }
        touch();
    }

    // 다른 복사본(같은 id)의 최신 정보를 이 객체에 반영한다. (파일마다 객체 복사본이 따로 저장되기 때문)
    public void sync(User source) {
        this.userName = source.userName;
        this.email = source.email;
        this.updatedAt = source.updatedAt;
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
