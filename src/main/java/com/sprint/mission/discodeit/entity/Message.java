package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    UUID channelId;
    UUID userId;
    String content;

    public Message(){
        super();
    }

     public Message(UUID userId, UUID channelId, String content){
        this();
        this.userId=userId;
        this.channelId=channelId;
        this.content=content;
    }

    public UUID getChannelId() {return channelId;}
    public UUID getUserId() {return userId;}
    public String getContent() {return content;}

    public void setContent(String message) {
        this.content = message;
        this.updateTime();
    }
}
