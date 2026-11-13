package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {
  private final MessageService messageService;

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments)
      throws IOException {
    List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();
    if (attachments != null) {
      for (MultipartFile file : attachments) {
        if (!file.isEmpty()) {
          attachmentRequests.add(
              new BinaryContentCreateRequest(
                  file.getOriginalFilename(), file.getContentType(), file.getBytes()));
        }
      }
    }

    MessageDto message = messageService.create(request, attachmentRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @Override
  @PatchMapping(value = "/{messageId}")
  public ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId, @RequestBody MessageUpdateRequest request) {
    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @Override
  @DeleteMapping(value = "/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<List<MessageDto>> findAllByChannelId(@RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }
}
