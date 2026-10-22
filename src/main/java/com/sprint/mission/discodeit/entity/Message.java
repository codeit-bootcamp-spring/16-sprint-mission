package com.sprint.mission.discodeit.entity;




public class Message extends BaseEntity {
;
    private String comment;
    private User user;
    private Channel channel;

    public User getUser() {
        return this.user;
    }

    public String getComment() {

        return this.comment;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public Message(String comment, User user, Channel channel) {

        this.comment = comment;
        this.user = user;
        this.channel = channel;
    }

    public void update(String comment) {
        this.comment = comment;
        updateTime();
    }

}
