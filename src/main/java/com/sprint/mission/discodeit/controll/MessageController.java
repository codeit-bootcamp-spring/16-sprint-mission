package com.sprint.mission.discodeit.controll;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> create(
            @RequestPart("message") MessageCreateDto dto,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageDto message = messageService.createMessage(dto, attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId
    ) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PATCH,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageDto> update(
            @PathVariable("id") UUID id,
            @RequestPart("message") MessageUpdateDto dto,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) {
        MessageUpdateDto updateDto = new MessageUpdateDto(id, dto.content());
        return ResponseEntity.ok(messageService.updateMessage(updateDto, attachments));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}