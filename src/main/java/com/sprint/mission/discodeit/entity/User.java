package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends BaseEntity {
    String name;

    public User(){
        super();
    }
    public User(String name){
         this();
         this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
