package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.messages.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.messages.MessageUpdateRequest;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 생성 기능(메시지 생성)
    MessageDto createMessage(MessageCreateRequest request, List<MultipartFile> attachments);

    // 단건 조회 기능(메시지 id 조회)
    MessageDto readMessage(UUID id);

    // 전체 조회 기능
    List<MessageDto> readAllMessage();

    List<MessageDto> findAllByChannelId(UUID channelId);

    // 수정 기능(메시지 ID와 새로운 내용 전달)
    MessageDto updateMessage(UUID messageId, MessageUpdateRequest request);

    // 삭제 기능(메시지 id를 찾아서 메시지 삭제)
    void deleteMessage(UUID id);
}