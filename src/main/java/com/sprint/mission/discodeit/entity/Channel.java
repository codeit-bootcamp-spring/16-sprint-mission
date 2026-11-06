package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.type.ChannelType;
import lombok.Getter;

import java.util.*;

// Channel 클래스는 BaseEntity 클래스로부터 상속한다.
public class Channel extends BaseEntity {

    /* Lombok을 통해 name의 Getter만 자동 생성 */
    @Getter
    private final ChannelType type;

    // 현재 name은 final이므로 변경할 수 없어.
    // 따라서 아래 코드에서 final을 제거해야 해.
    @Getter
    private String name;

    @Getter
    private final String description;

    // 채널 참가된 유저들을 순서대로 받기위해 LinkedHashSet 으로 묶는다.
    private final Set<UUID> memberIds = new LinkedHashSet<>();

    // PUBLIC 채널 생성
    public Channel(String name, String description) {
        super(); // 부모 클래스 생성자에서 ID와 생성·수정 시각을 초기화한다.

        // 채널 이름을 생성할 때 null값 이거나 공백일때 예외 처리하는 검사입니다.
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        this.type = ChannelType.PUBLIC;
        this.name = name;
        this.description = description;
    }

    // PRIVATE 채널 생성
    public Channel(List<UUID> memberIds) {
        super();

        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널 참여자는 필수입니다.");
        }

        this.type = ChannelType.PRIVATE;
        this.name = null;
        this.description = null;

        for (UUID userId : memberIds) {
            addMember(userId);
        }
    }

    public Set<UUID> getMemberIds() {
        return new LinkedHashSet<>(memberIds);
    }

    public void addMember(UUID userId){
        // 유저를 채널에 참가시킨다.
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

    // 전달받은 채널 이름으로 현재 객체의 이름을 변경한다.
    public void changeName(String name){
        if (type == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // 이름이 null 이거나 공백이면 예외 처리한다.
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        this.name = name;
        touch(); // 수정 시각(updateAt)을 현재 시각으로 갱신한다.
    }

    @Override
    public String toString() {
        return "Channel{id:'" + getId()
                + "', type:'" + type
                + "', name:'" + name
                + "', description:'" + description
                + "', memberIds:" + memberIds
                + ", createdAt:" + getCreatedAt()
                + ", updatedAt:" + getUpdatedAt()
                + "}\n";
    }
}