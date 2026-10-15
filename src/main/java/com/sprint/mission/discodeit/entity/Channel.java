package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    String name;
    final Set<UUID> userId;
    List<String> messageList;

    public Channel() {
        super();
        this.userId=new HashSet<>();
    }
    public Channel(String name){
        this();
        this.name=name;
    }

    public String getName() {
        return name;
    }
    public Set<UUID> getUserId() { return userId; }
    public List<String> getMessageList() {
        return messageList;
    }

    public void setName(String name) {
        this.name = name;
        this.updateTime();
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
        this.updateTime();
    }
    public void addUser(UUID userId) { this.userId.add(userId); }
    public void removeUser(UUID userId) { this.userId.remove(userId);}
}



