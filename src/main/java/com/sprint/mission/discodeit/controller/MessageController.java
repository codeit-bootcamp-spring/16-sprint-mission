package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;


    // 메시지 생성 (JSON)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Message> createJson(@RequestBody MessageCreateRequest messageCreateRequest) {
        Message createdMessage = messageService.create(messageCreateRequest, new ArrayList<>());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }
    // 메시지 생성 (JSON + 첨부파일 여러 개)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> attachmentRequests = toBinaryContentRequests(attachments);
        Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    // 채널별 메시지 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findAllByChannelId(channelId));
    }

    // 메시지 수정
    @RequestMapping(path = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<Message> update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        Message updatedMessage = messageService.update(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
    }

    // 메시지 삭제
    @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 업로드된 파일들을 서비스가 받는 형태로 바꿔주는 도우미 메서드
    private List<BinaryContentCreateRequest> toBinaryContentRequests(List<MultipartFile> files) {
        List<BinaryContentCreateRequest> requests = new ArrayList<>();
        if (files == null) {
            return requests;
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                requests.add(new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return requests;
    }
}