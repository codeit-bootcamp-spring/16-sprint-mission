package com.sprint.mission.discodeit.entity.addNewDomain;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter // Lombok을 통해 모든 필드의 Getter 메서드를 자동 생성한다.
public class BinaryContent implements Serializable {

    // static 필드는 기본적으로 Getter에서 제외대상 입니다.
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id; // 바이너리 콘텐츠 식별자
    private final Instant createdAt; // 생성 시각
    private final String fileName; // 파일 이름
    private final String contentType; // 파일의 MIME 타입
    private final Long size; // 파일 크기

    // 바이너리 콘텐츠 생성 시 식별자와 생성 시각을 초기화하고 전달받은 파일 정보를 저장합니다.
    public BinaryContent(String fileName, String contentType, Long size) {
        this.id = UUID.randomUUID();    // 고유 식별자 생성
        this.createdAt = Instant.now(); // 현재 시각으로 생성 시각 초기화
        this.fileName = fileName;       // 파일 이름 저장
        this.contentType = contentType; // 파일의 MIME 타입 저장
        this.size = size;               // 파일 크기 저장
    }
}
