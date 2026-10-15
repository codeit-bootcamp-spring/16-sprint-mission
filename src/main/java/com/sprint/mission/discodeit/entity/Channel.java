package com.sprint.mission.discodeit.entity;


import java.util.ArrayList;
import java.util.List;


public class Channel extends BaseEntity{
//    private UUID id;
    private String name;
//    private Long createdAt;
//    private Long updatedAt;


    public Channel(String name) {
//        this.id = UUID.randomUUID();
        this.name = name;
//        this.createdAt = Instant.now().getEpochSecond();
//        this.updatedAt = this.createdAt;

        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }
    private List<User> users;
    private List<Message> messages;

//    public UUID getId() {
//
//        return this.id;
//    }

    public String getName() {

        return this.name;
    }

//    public Long getCreatedAt() {
//
//        return this.createdAt;
//    }
//
//    public Long getUpdatedAt() {
//
//        return this.updatedAt;
//    }

    public List<User> getUsers() {
        return this.users;
    }
    public List<Message> getMessages() {
        return this.messages;
    }


    public void update(String name) {
        this.name = name;
        updateTime();
    }

}


