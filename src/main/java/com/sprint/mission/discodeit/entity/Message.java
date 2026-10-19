package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {

    private String content; // 메시지 내용
    private User user; // 메시지를 작성한 사용자
    private Channel channel; // 메시지가 작성된 채널

    public Message(String content, User user, Channel channel) {
        super(); // 부모 클래스에서 ID와 생성 시간 초기화
        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    public String getContent() {
        return content; // 메시지 내용 반환
    }

    public User getUser() {
        return user; // 작성자 반환
    }

    public Channel getChannel() {
        return channel; // 채널 반환
    }

    public void update(String content) {
        this.content = content; // 메시지 내용 수정
        updateUpdatedAt(); // 수정 시간 갱신
    }
}

