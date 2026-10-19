package com.sprint.mission.discodeit.service; // 서비스 패키지

import com.sprint.mission.discodeit.entity.Message; // Message 클래스 사용
import java.util.List; // 메시지 목록을 위한 List
import java.util.UUID; // 메시지 ID 자료형

public interface MessageService { // 메시지 서비스 인터페이스

    Message create(Message message); // 메시지 생성
    Message findById(UUID id); // ID로 메시지 한 개 조회
    List<Message> findAll(); // 모든 메시지 조회
    Message update(UUID id, Message message); // 메시지 정보 수정
    void delete(UUID id); // 메시지 삭제

}