package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class User extends BaseEntity{

    private String name;
    private String password;
    private String email;
    private UUID profileId;


    public User(String name, String password, String email) {

        this.name = name;
        this.password = password;
        this.email = email;

        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void updateProfile(UUID profileId) {
        this.profileId = profileId;
        updateTime();
    }

    private List<Channel> channels;
    private List<Message> messages;

    public void update(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        updateTime();
    }
}
