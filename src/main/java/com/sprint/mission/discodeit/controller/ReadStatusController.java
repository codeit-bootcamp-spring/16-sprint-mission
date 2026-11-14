package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.status.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.status.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성(POST)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> postReadStatus(
            @RequestBody ReadStatusCreateRequest request) {

        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    // 특정 사용자의 메시지 수신 정보 조회(GET)
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> getAllByUserId(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    // 메시지 읽음 상태 수정(PATCH)
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ReadStatus putReadStatus(
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request) {

        return readStatusService.update(readStatusId, request);
    }
}
