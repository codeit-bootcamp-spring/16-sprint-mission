package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class Channel extends BaseEntity{

    private String name;
    private ChannelType type;
    private String description;


    public Channel(ChannelType type,String name, String description) {

        this.name = name;
        this.type = type;
        this.description = description;


    }




    public void update(String name,String description) {
        this.name = name;
        this.description = description;
        updateTime();
    }


}


