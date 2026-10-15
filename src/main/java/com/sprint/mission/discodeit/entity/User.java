package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private String username;
    private String password;
    private String phoneNumber;
    private String email;

    public User(String username, String password, String phoneNumber, String email) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        }

    public UUID getId() {
        return id;
        }
    public Long getCreatedAt() {
        return createdAt;
        }
    public Long getUpdatedAt() {
        return updatedAt;
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
        this.updatedAt = System.currentTimeMillis();
    }
}