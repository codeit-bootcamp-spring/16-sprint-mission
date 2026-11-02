package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class User extends BaseEntity{

    private static final long serialVersionUID = 1L;

    private String name;
    private String password;
    private String email;
    private UUID profileId;



    public User(String name, String password, String email) {

        this.name = name;
        this.password = password;
        this.email = email;

    }

    public void updateProfile(UUID profileId) {
        this.profileId = profileId;
        updateTime();
    }


    public void update(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        updateTime();
    }
}
