package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class Channel extends BaseEntity {
    String name;
    List<String> messageList;

    public Channel() {
        super();
    }
    public Channel(String name){
        this();
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
        this.updatedAt = System.currentTimeMillis();
    }

}



