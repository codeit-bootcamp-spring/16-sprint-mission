package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends BaseEntity {
    String name;
    final Set<UUID> channels;

    public User(){
        super();
        this.channels=new HashSet<>();
    }
    public User(String name){
         this();
         this.name=name;
    }

    public String getName() {
        return name;
    }
    public Set<UUID> getChannels() { return channels; }

    public void setName(String name) {
        this.name = name;
        this.updateTime();
    }

    public void joinChannel(UUID channelId) { this.channels.add(channelId); }
    public void leaveChannel(UUID channelId){ this.channels.remove(channelId); }
}
