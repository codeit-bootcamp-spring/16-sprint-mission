package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends Base{
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void update(String name, int age) {
        this.name = name;
        this.age = age;
        updateUpdatedAt(System.currentTimeMillis());
    }
}
