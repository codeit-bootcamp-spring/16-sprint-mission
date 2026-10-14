package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class Channel {
    UUID id;
    String name;
    //String description;
    Long createdAt;
    Long updatedAt;
    List<String> messageList;

    public Channel() {
        this.id = UUID.randomUUID();
        Long currentTime= System.currentTimeMillis();
        this.createdAt=currentTime;

    }
    public Channel(String name){
        this();
        this.name=name;
    }
/*    public Channel(String name, List<String> messageList){
        this();
        this.name=name;
        this.messageList=new ArrayList<>();

    }*/

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }

   /* public String getDescription() {
        return description;
    }*/

    public Long getCreatedAt() {
        return createdAt;
    }
    public Long getUpdatedAt() {
        return updatedAt;
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

    /*public void setDescription(String description) {
        this.description = description;
    }*/
 /*   @Override
    public String toString() {
        return "Channel name: "+name;
    }
*/


}



