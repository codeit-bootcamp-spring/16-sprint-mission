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
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 1. 메시지 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST)
    public ReadStatus create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        return readStatusService.create(request);
    }

    // 2. 메시지 수신 정보 수정
    @RequestMapping(
            value = "/{readStatusId}",
            method = RequestMethod.PATCH
    )
    public ReadStatus update(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        return readStatusService.update(readStatusId, request);
    }

    // 3. 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return readStatusService.findAllByUserId(userId);
    }
}
