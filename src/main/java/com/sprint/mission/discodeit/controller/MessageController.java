package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/messages")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findAllMessagesByChannelId(@NotNull @RequestParam UUID channelId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.messageService.findAllByChannelId(channelId));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createMessage(@RequestPart(name = "attachments", required = false) List<MultipartFile> attachments,
                                                         @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                         @Valid @RequestPart("messageCreateRequest") MessageCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.messageService.create(createRequest, attachments));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponse> updateMessage(@NotNull @PathVariable UUID messageId,
                                                         @RequestBody MessageUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.messageService.update(messageId, updateRequest));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@NotNull @PathVariable UUID messageId) {
        this.messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
