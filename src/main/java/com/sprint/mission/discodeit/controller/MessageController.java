
package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
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

    @RequestMapping(method = RequestMethod.GET)
    public List<Message> findAll(
            @RequestParam("channelId") UUID channelId
    ) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH
    )
    public Message update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.DELETE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }

    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.GET
    )
    public Message find(@PathVariable UUID messageId) {
        return messageService.find(messageId);
    }
}
