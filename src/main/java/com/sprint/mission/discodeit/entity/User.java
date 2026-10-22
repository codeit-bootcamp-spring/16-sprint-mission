package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import java.io.Serializable;

public class User extends BaseEntity implements Serializable {

    private static final Long serialVersionUID = 1L;

    private String username;
    private String password;
    private String phoneNumber;
    private String email;

    public User(String username, String password, String phoneNumber, String email) {
        super();
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        }

    public String getUsername() {
        return username;
        }
    public String getPassword() {
        return password;
        }
    public String getPhoneNumber() {
        return phoneNumber;
        }
    public String getEmail() {
        return email;
        }

    public void update(String username, String password, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        updateTimestamp();
    }
}