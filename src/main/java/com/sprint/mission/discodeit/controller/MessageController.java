package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageRequestDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> create(@RequestBody MessageRequestDto messageRequestDto) {
        MessageResponseDto messageResponseDto = messageService.create(messageRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponseDto);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable UUID messageId, @RequestBody MessageUpdateDto messageUpdateDto) {
        MessageResponseDto messageResponseDto = messageService.update(messageId, messageUpdateDto);
        return ResponseEntity.ok(messageResponseDto);
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<List<MessageResponseDto>> findAllByChannelId(@PathVariable UUID channelId) {
        List<MessageResponseDto> messageResponseDtoList = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messageResponseDtoList);
    }
}
