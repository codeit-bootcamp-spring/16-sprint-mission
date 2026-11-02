package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    private String comment;
    private UUID authorId;
    private UUID channelId;

    public Message(String comment,UUID authorId, UUID channelId) {

        this.comment = comment;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public void update(String comment) {
        this.comment = comment;
        updateTime();
    }

}
