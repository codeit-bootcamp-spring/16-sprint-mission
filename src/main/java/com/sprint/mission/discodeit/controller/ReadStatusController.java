package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;
    // 특정 채널의 메시지 수신 정보를 생성
    @PostMapping
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }
    //  특정 채널의 메시지 수신 정보를 수정
    @PutMapping("/{readStatusId}")
    public ReadStatus updateReadStatus(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }
    // 특정 사용자의 메시지 수신 정보를 조회
    @GetMapping("/user/{userId}")
    public List<ReadStatus> getReadStatusesByUserId(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}
