package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Message 클래스는 BaseEntity 클래스로부터 상속한다.
@Getter // Lombok을 통해 모든 필드의 Getter 메서드를 자동 생성한다.
public class Message extends BaseEntity {
    // 메시지의 필드 선언.
    private String content;
    private final UUID plannerId;
    private final UUID channelId;
    private List<UUID> attachmentIds; // 첨부파일의 BinaryContent 식별자 목록

    public Message(String content, UUID plannerId,
                   UUID channelId, List<UUID> attachmentIds) {
        super(); // 부모 클래스 생성자에서 ID와 생성·수정 시각을 초기화한다.

        // 메시지 내용을 적을 때 null값 이거나 공백일때 예외 처리하는 검사입니다.
        if (content == null || content.isBlank()){
            throw new IllegalArgumentException("메시지 내용을 적어주세요.");
        }
        if (plannerId == null){
            throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        }
        if (channelId == null){
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        // 검사가 끝나면 전달받은 메시지 내용을 필드에 저장한다.
        this.content = content;
        this.plannerId = plannerId;
        this.channelId = channelId;

        // 첨부파일 식별자 목록을 복사하여 저장한다.
        this.attachmentIds = attachmentIds == null
                ? new ArrayList<>()
                : new ArrayList<>(attachmentIds);
    }

    // 전달받은 메시지 내용으로 현재 객체의 내용을 변경한다.
    public void changeContent(String content){
        // 메시지 내용이 null 이거나 공백이면 예외 처리한다.
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException("메시지 내용을 적어주세요");
        }
        this.content = content;
        touch(); // 수정 시각(updateAt)을 현재 시각으로 갱신한다.
    }

    @Override
    public String toString() {
        return "Message{id:'" + getId() + "'},{" + "content:'" + content + "'}, {createdAt: "
                + getCreatedAt() + " },{updatedAt: " + getUpdatedAt() + " }\n";
    }
}