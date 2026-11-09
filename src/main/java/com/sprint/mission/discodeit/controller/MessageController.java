package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("v1/message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody CreateMessageRequest request) {
        MessageResponse response = messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<MessageResponse> updateMessage(@RequestBody UpdateMessageRequest request,
                                                         @RequestBody UUID messageId) {
        MessageResponse response = messageService.update(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{message-id}")
    public ResponseEntity deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{channel-id}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable("channel-id") UUID channelId) {
        List<MessageResponse> response = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
