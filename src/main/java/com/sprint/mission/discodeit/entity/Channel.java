package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

// Channel 클래스는 BaseEntity 클래스로부터 상속한다.
public class Channel extends BaseEntity {
    // 채널의 필드 선언.
    private String name;

    // 채널 참가된 유저들을 순서대로 받기위해 LinkedHashSet 으로 묶는다.
    private final Set<UUID> memberIds = new LinkedHashSet<>();

    public Set<UUID> getMemberIds() {
        return new HashSet<>(memberIds);
    }

    // 유저를 채널에 참가시킨다.
    public void addMember(UUID userId){
        if (userId == null){
            throw new IllegalArgumentException("유저 ID는 필수입니다.");
        }
        if (!memberIds.add(userId)){
            throw new IllegalArgumentException("이미 채널에 참여중인 유저입니다.");
        }
        touch();
    }

    // 유저를 채널에 퇴장시킨다.
    public void exitMember(UUID userId){
        if(!memberIds.remove(userId)){
            throw new IllegalArgumentException("채널에 참여중인 유저가 아닙니다.");
        }
        touch();
    }

    // 유저가 채널에 참여 중인지 확인한다.
    public boolean hasMember(UUID userId){
        return memberIds.contains(userId);
    }

    // Channel 객체를 생성할 때 채널 이름을 전달받는다.
    public Channel(String name) {
        super(); // 부모 클래스 생성자에서 ID와 생성·수정 시각을 초기화한다.

        // 채널 이름을 생성할 때 null값 이거나 공백일때 예외 처리하는 검사입니다.
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }
        // 검사가 끝나면 전달받은 채널 이름을 필드에 저장한다.
        this.name = name;
    }

    // 저장된 채널 이름을 반환하는 Getter 메서드입니다.
    public String getName() {
        return name;
    }

    // 전달받은 채널 이름으로 현재 객체의 이름을 변경한다.
    public void changeName(String name){
        // 이름이 null 이거나 공백이면 예외 처리한다.
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }
        this.name = name;
        touch(); // 수정 시각(updateAt)을 현재 시각으로 갱신한다.
    }

    @Override
    public String toString() {
        return "Channel{id:'" + getId() + "'},{" + "name:'" + name + "'}, {createdAt: "
                + getCreatedAt() + " },{updatedAt: " + getUpdatedAt() + " }\n";
    }
}