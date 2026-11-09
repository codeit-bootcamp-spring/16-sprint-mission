package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    // 메시지 작성
    @PostMapping
    public Message createMessage(@RequestBody MessageCreateRequest request) {
        return messageService.create(request, new ArrayList<>());
    }

    // 특정 채널의 모든 메시지 조회
    @GetMapping("/channel/{channelId}")
    public List<Message> getMessagesByChannel(@PathVariable UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    // 메시지 수정
    @PutMapping("/{messageId}")
    public Message updateMessage(@PathVariable UUID messageId, @RequestBody MessageUpdateRequest request) {
        return messageService.update(messageId, request);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}