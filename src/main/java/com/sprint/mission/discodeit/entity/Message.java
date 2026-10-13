package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {

    private String content;

    public Message(String content) {
        super();
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.update();
        this.content = content;
    }
}
