package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    UUID id;
    UUID channelId;
    UUID userId;
    String content;


    Long createdAt;
    //Long updatedAt;

    public Message(){
        this.id = UUID.randomUUID();
        Long currentTime= System.currentTimeMillis();
        this.createdAt=currentTime;
        //this.updatedAt=currentTime;

    }
     public Message(UUID userId, UUID channelId, String content){
        this();
        this.userId=userId;
        this.channelId=channelId;
        this.content=content;
    }

    public UUID getId() {return id;}
    public UUID getChannelId(){return channelId;}
    public UUID getUserId() {return userId;}

    public String getContent() {return content;}
    public Long getCreatedAt() {return createdAt;}
   // public Long getUpdatedAt() {return updatedAt;}

    public void setContent(String message) {this.content = message;}


/*
    @Override
    public String toString() {
        return "Message: "+content;
    }
*/









}
