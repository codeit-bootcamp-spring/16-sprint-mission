package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {

    private String channel; // 채널 이름

    public Channel(String channel) {
        super(); // 부모 클래스의 생성자 실행
        this.channel = channel; // 채널 이름 저장
    }

    public String getChannel() {
        return channel; // 채널 이름 반환
    }

    public void update(String channel) {
        this.channel = channel; // 채널 이름 수정
        updateUpdatedAt(); // 수정 시간 갱신
    }
}