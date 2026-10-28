package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class Channel extends BaseEntity{

    private String name;


    public Channel(String name) {

        this.name = name;

        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }
    private List<User> users;
    private List<Message> messages;



    public void update(String name) {
        this.name = name;
        updateTime();
    }


}


