package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(value = "/channels/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageFindResponse>> findAllMessagesByChannelId(@NotNull @PathVariable UUID channelId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.messageService.findAllByChannelId(channelId));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageCreateResponse> createMessage(@RequestPart(name = "attachments", required = false) List<MultipartFile> attachments,
                                                               @Valid @RequestPart("message") MessageCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.messageService.create(createRequest, attachments));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageUpdateResponse> updateMessage(@NotNull @PathVariable UUID id,
                                                               @RequestBody MessageUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.messageService.update(id, updateRequest));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@NotNull @PathVariable UUID id) {
        this.messageService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
