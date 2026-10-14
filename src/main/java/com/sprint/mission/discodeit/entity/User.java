package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    UUID id;
    String name;
    Long createdAt;
    Long updatedAt;
    public User(){
        this.id = UUID.randomUUID();
        Long currentTime= System.currentTimeMillis();
        this.createdAt=currentTime;
        this.updatedAt=currentTime;
    }
    public User(String name){
        //this.id=id;
         this();
         this.name=name;



    }


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

/*    @Override
    public String toString() {
        return "User name: "+name;
    }*/


}
