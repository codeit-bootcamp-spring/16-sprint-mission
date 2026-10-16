package com.sprint.mission.discodeit.entity;


import java.util.ArrayList;
import java.util.List;


public class User extends BaseEntity{
    //private UUID id;
    private String name;
    private String password;
    private String email;
   //private Long createdAt;
   // private Long updatedAt;

    public User(String name, String password, String email) {
       //this.id = UUID.randomUUID();
        this.name = name;
        this.password = password;
        this.email = email;
        //this.createdAt = Instant.now().getEpochSecond();
       // this.updatedAt = this.createdAt;

        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    private List<Channel> channels;
    private List<Message> messages;

   // public UUID getId() {
        //return this.id;
  //  }
    public String getName() {
        return this.name;
    }
    public String getPassword() {
        return this.password;
    }
    public String getEmail() {return this.email;}
//    public Long getCreatedAt() {
//        return this.createdAt;
//    }
//    public Long getUpdatedAt() {
//        return this.updatedAt;
//    }
    public List<Channel> getChannels() {
        return this.channels;
    }
    public List<Message> getMessages() {
        return this.messages;
    }

    public void update(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        updateTime();
    }

}
