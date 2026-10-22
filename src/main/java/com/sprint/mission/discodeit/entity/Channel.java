package com.sprint.mission.discodeit.entity;


import java.util.ArrayList;
import java.util.List;


public class Channel extends BaseEntity{

    private String name;


    public Channel(String name) {

        this.name = name;

        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }
    private List<User> users;
    private List<Message> messages;


    public String getName() {

        return this.name;
    }



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


