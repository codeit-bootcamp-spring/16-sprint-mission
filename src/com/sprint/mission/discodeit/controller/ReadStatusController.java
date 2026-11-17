package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusDto readStatusDto = readStatusService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatusDto);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(
            value = "/users/{userId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                readStatusService.findAllByUserId(userId)
        );
    }

    // 메시지 수신 정보 수정
    @RequestMapping(
            value = "/{readStatusId}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ReadStatusDto> update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusUpdateRequest updateRequest = new ReadStatusUpdateRequest(
                readStatusId,
                request.lastReadAt()
        );

        return ResponseEntity.ok(
                readStatusService.update(updateRequest)
        );
    }
}