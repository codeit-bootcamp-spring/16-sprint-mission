
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.ArrayList;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Message create(
            @RequestPart("request") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false)
            List<MultipartFile> attachments
    ) throws IOException {

        List<BinaryContentCreateRequest> files = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                if (!attachment.isEmpty()) {
                    files.add(new BinaryContentCreateRequest(
                            attachment.getOriginalFilename(),
                            attachment.getContentType(),
                            attachment.getBytes()
                    ));
                }
            }
        }

        return messageService.create(request, files);
    }


    @GetMapping
    public List<Message> findAll(@RequestParam("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @PatchMapping("/{messageId}")
    public Message update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }



}
