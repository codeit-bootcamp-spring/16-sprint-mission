package com.sprint.mission.discodeit.service; // 서비스 패키지

import com.sprint.mission.discodeit.entity.Channel; // Channel 클래스 사용
import java.util.List; // 채널 목록을 위한 List
import java.util.UUID; // 채널 ID 자료형

public interface ChannelService { // 채널 서비스 인터페이스

    Channel create(Channel channel); // 채널 생성
    Channel findById(UUID id); // ID로 채널 한 개 조회
    List<Channel> findAll(); // 모든 채널 조회
    Channel update(UUID id, Channel channel); // 채널 정보 수정
    void delete(UUID id); // 채널 삭제

}