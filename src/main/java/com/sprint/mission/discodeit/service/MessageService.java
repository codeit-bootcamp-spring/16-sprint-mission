package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MessageService {

    // 생성
    Message createMessage(Message message);

    // 조회
    Message getMessageById(UUID messageId);
    List<Message> getAllMessages();
    List<Message> getMessagesByUserId(UUID userId);
    List<Message> getMessagesByChannelId(UUID channelId);
    List<Message> getMessagesByUserAndChannel(UUID userId, UUID channelId);

    // 수정
    Message updateMessage(UUID messageId, UUID userId, String content);

    // 삭제
    void deleteMessage(UUID messageId, UUID userId); // 직접 메시지 삭제 (참여자인지 검증)
    void deleteMessagesByChannelId(UUID channelId);  // 채널 삭제 시 참여자인지 검증 안하고 삭제
    void deleteMessagesByUserId(UUID userId);        // 회원 탈퇴 시 참여자인지 검증 안하고 삭제
}
