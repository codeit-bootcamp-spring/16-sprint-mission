package com.sprint.mission.discodeit.entity;


import lombok.Getter;

@Getter
public class Message extends BaseEntity {
;
    private String comment;
    private User user;
    private Channel channel;

    public Message(String comment, User user, Channel channel) {

        this.comment = comment;
        this.user = user;
        this.channel = channel;
    }

    public void update(String comment) {
        this.comment = comment;
        updateTime();
    }
    public void updateUser(User user) {
        this.user = user;
    }

    public void updateChannel(Channel channel) {
        this.channel = channel;
    }

}
