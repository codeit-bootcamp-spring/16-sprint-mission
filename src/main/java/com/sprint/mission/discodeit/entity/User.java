package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {

    private String username; // 사용자 이름

    public User(String username) {
        super(); // 부모 클래스(BaseEntity)의 생성자 실행
        this.username = username; // 사용자 이름 저장
    }

    public String getUsername() {
        return username; // 사용자 이름 반환
    }

    public void update(String username) {
        this.username = username; // 사용자 이름 수정
        updateUpdatedAt(); // 수정 시간 갱신
    }
}