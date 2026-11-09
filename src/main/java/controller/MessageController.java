package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

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

    // 1. 메시지 생성 (첨부파일 포함)
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Message create(
            @RequestPart("messageCreateRequest")
            MessageCreateRequest request,

            @RequestPart(
                    value = "attachments",
                    required = false
            )
            List<MultipartFile> attachments
    ) throws IOException {

        // 첨부파일을 서비스 요청 객체로 변환
        List<BinaryContentCreateRequest> files =
                new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile file : attachments) {

                files.add(
                        new BinaryContentCreateRequest(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes()
                        )
                );
            }
        }

        // 기존 메시지 생성 서비스 호출
        return messageService.create(request, files);
    }

    // 2. 메시지 수정
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

    // 3. 메시지 삭제
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.DELETE
    )
    public void delete(
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);
    }

    // 4. 특정 채널의 메시지 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<Message> findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        return messageService.findAllByChannelId(channelId);
    }
}

