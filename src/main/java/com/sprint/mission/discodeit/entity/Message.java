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
         if (userId == null || channelId == null) {
             throw new NullPointerException("ID 값들은 null일 수 없습니다.");
         }
         if (content == null || content.trim().isEmpty()) {
             throw new NullPointerException("내용이 비어있습니다.");
         }
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
