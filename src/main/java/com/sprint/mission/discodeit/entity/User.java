package com.sprint.mission.discodeit.entity;

import java.util.UUID;

//서비스를 사용하는 사용자 정보
public class User extends BaseEntity{
    private static final long serialVersionUID = 1L;

    // User field
    private String username;
    private String email;
    private String password;

    // 생성자를 통해서만 초기화
    // setter 메서드 제거
    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public void update(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        updateUpdatedAt();
    }

}
