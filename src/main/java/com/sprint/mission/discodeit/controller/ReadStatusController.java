package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @PostMapping
    public ResponseEntity<ReadStatus> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @PutMapping("/{readStatusId}")
    public ResponseEntity<ReadStatus> updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatus updatedStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(updatedStatus);
    }

    // 특정 사용자의 메시지 수신 정보 목록 조회
    @GetMapping("/users/{userId}/read-statuses")
    public ResponseEntity<List<ReadStatus>> getReadStatusesByUserId(@PathVariable UUID userId) {
        List<ReadStatus> statuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(statuses);
    }
}