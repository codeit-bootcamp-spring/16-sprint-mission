package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController // 반환한 메시지 정보를 JSON 응답으로 전달합니다.
@RequestMapping("/api/messages") // 메시지 API의 공통 주소입니다.
@RequiredArgsConstructor // MessageService를 주입받는 생성자를 만듭니다.
public class MessageController {

    private final MessageService messageService;

    // 1. 메시지 전송
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> create(
            @RequestBody MessageCreateRequest request
    ) {
        // 서비스가 작성자와 채널의 존재를 확인하고 메시지와 첨부 파일을 저장합니다.
        Message message = messageService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // 2. 메시지 수정
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> update(
            @PathVariable("messageId") UUID messageId, // URL에서 수정 대상 ID를 받습니다.
            @RequestBody MessageUpdateRequest request // JSON에서 변경할 내용을 받습니다.
    ) {
        // 수정 대상은 URL의 ID로 결정합니다.
        MessageUpdateRequest serviceRequest = new MessageUpdateRequest(
                messageId,
                request.content()
        );

        // 기존 서비스는 메시지 내용만 수정합니다.
        messageService.update(serviceRequest);

        // update()에 반환값이 없으므로 수정된 메시지를 다시 조회합니다.
        Message message = messageService.findById(messageId);

        return ResponseEntity.ok(message);
    }

    // 3. 메시지 삭제
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @PathVariable("messageId") UUID messageId
    ) {
        // 메시지와 연결된 첨부 파일을 함께 삭제합니다.
        messageService.delete(messageId);

        return ResponseEntity.noContent().build(); // 204, 응답 본문 없음
    }

    // 4. 특정 채널의 메시지 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId // ?channelId= 값을 받습니다.
    ) {
        // 서비스가 채널의 존재를 확인하고 해당 채널의 메시지만 조회합니다.
        List<Message> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(messages);
    }
}