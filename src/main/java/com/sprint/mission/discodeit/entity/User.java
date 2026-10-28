package com.sprint.mission.discodeit.entity;

// User 클래스는 BaseEntity 클래스로부터 상속한다.
public class User extends BaseEntity {
    // 유저의 필드 선언
    private String name;

    // User 객체를 생성할 때 사용자 이름을 전달받는다.
    public User(String name) {
        super(); // 부모 클래스 생성자에서 ID와 생성·수정 시각을 초기화한다.

        // 사용자 이름을 생성할 때 null값 이거나 공백일때 예외 처리하는 검사입니다.
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        // 검사가 끝나면 전달받은 사용자 이름을 필드에 저장한다.
        this.name = name;
    }

    // 저장된 사용자 이름을 반환하는 Getter 메서드입니다.
    public String getName() {
        return name;
    }

    // 전달받은 유저 이름으로 현재 객체의 이름을 변경한다.
    public void changeName(String name){
        // 이름이 null 이거나 공백이면 예외 처리한다.
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        this.name = name;
        touch(); // 수정 시각(updatedAt)을 현재 시각으로 갱신한다.
    }

    @Override
    public String toString() {
        return "User{id:'" + getId() + "'},{" + "name:'" + name + "'},{createdAt: "
                + getCreatedAt() + " },{updatedAt: " + getUpdatedAt() + " }\n";
    }
}