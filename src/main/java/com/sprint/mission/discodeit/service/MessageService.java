package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    //생성
    Message save(Message message);
    //조회
    List<Message> findId(UUID id);
    //전체 조회
    List<Message> findAll();
   //수정
    Message update(UUID id, String content);
    //특정채널 메세지 모두 조회
    List<Message> findChannelId(UUID channelId);
    List<Message> findUserlId(UUID userId);
    //삭제
    void deleteId(UUID id);
    void deleteByChannelId(UUID channelId);
    void deleteByUserId(UUID userId);
}
