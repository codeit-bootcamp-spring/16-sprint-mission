package com.sprint.mission.discodeit.entity;




public class Message extends BaseEntity {
//    private UUID id;
//    private Long createdAt;
//    private Long updatedAt;
    private String comment;
    private User user;
    private Channel channel;

    public User getUser() {
        return this.user;
    }

    public String getComment() {

        return this.comment;
    }
//
//    public Long getCreatedAt() {
//
//        return this.createdAt;
//    }
//
//    public Long getUpdatedAt() {
//
//        return this.updatedAt;
//    }

//    public UUID getId() {
//        return this.id;
//    }
    public Channel getChannel() {
        return this.channel;
    }

    public Message(String comment, User user, Channel channel) {
//        this.id = UUID.randomUUID();
        this.comment = comment;
//        this.createdAt = Instant.now().getEpochSecond();
//        this.updatedAt = this.createdAt;
        this.user = user;
        this.channel = channel;
    }

    public void update(String comment) {
        this.comment = comment;
        updateTime();
    }
}
