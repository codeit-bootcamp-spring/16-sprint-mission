package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

// User 클래스는 BaseEntity 클래스로부터 상속한다.
@Getter // Lombok을 통해 Getter 메서드를 자동 생성한다.
public class User extends BaseEntity {
    // 유저의 필드 선언
    private String username;
    private String email;
    private String password;
    private UUID profileId; // 프로필 이미지의 BinaryContent 식별자

    public User(String username, String email,
                String password, UUID profileId) {
        super(); // 부모 클래스의 기본 생성자를 호출하여 ID와 생성·수정 시각을 초기화한다.

        // 이름이 null이거나 공백이면 예외를 발생시킨다.
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }

        this.username = username; // 유효성 검사를 통과한 이름을 필드에 저장한다.
        this.email = email;
        this.password = password;
        this.profileId = profileId; // 전달받은 프로필 이미지의 BinaryContent 식별자를 저장한다.
    }

    public User(User source, String username,
                String email, String password, UUID profileId) {
        super(source); // 부모 클래스의 복사 생성자를 호출하여 기존 유저의 ID와 생성·수정 시각을 복사한다.

        // 사용자 이름을 생성할 때 null값 이거나 공백일때 예외 처리하는 검사입니다.
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }

        this.username = username; // 유효성 검사를 통과한 이름을 필드에 저장한다.
        this.email = email;
        this.password = password;
        this.profileId = profileId; // 기존 사용자의 프로필 이미지 식별자를 복사한다.

        // 수정 시각을 현재 시각으로 갱신한다.
        touch();
    }

    // 전달받은 유저 이름으로 현재 객체의 이름을 변경한다.
    public void changeName(String username){
        // 이름이 null 이거나 공백이면 예외 처리한다.
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        this.username = username;
        touch(); // 수정 시각(updatedAt)을 현재 시각으로 갱신한다.
    }

    @Override
    public String toString() {
        return "User{id:'" + getId() + "'},{" + "username:'" + username + "'},{createdAt: "
                + getCreatedAt() + " },{updatedAt: " + getUpdatedAt() + " }\n";
    }
}