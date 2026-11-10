package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> sendMessage(@RequestPart("request") CreateMessageRequest request,
                                                       @RequestPart(value = "attachments", required = false) List<MultipartFile> files) throws IOException {
        List<BinaryContentRequest> attachments = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                attachments.add(new BinaryContentRequest(file.getOriginalFilename(), file.getSize(),
                        file.getContentType(), file.getBytes()));
            }
        }
        CreateMessageRequest newRequest = new CreateMessageRequest(request.content(), request.channelId(),
                request.authorId(), attachments);
        MessageResponse response = messageService.create(newRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update/{message-id}")
    public ResponseEntity<MessageResponse> updateMessage(@RequestBody UpdateMessageRequest request,
                                                         @PathVariable("message-id") UUID messageId) {
        return ResponseEntity.ok(messageService.update(messageId, request));
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
