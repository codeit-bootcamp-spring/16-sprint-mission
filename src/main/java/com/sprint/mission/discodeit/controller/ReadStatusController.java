package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@Tag(name = "읽음 상태", description = "채널별 읽음 상태 관리 API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @Operation(summary = "읽음 상태 등록", description = "사용자와 채널별로 마지막으로 읽은 시각을 등록하고 읽음 상태 반환")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request){
        return readStatusService.create(request);
    }

    @Operation(summary = "읽음 상태 수정", description = "읽음 상태 ID로 기록을 찾아 마지막으로 읽은 시각을 갱신하고 읽음 상태 반환")
    @RequestMapping(method = RequestMethod.PATCH,value = "/{readStatusId}")
    public ReadStatus updateReadStatus(@PathVariable UUID readStatusId,@RequestBody ReadStatusUpdateRequest request){
        ReadStatusUpdateRequest readStatusUpdateRequest = new ReadStatusUpdateRequest(readStatusId,request.newLastReadAt());
        return readStatusService.update(readStatusUpdateRequest);
    }

    @Operation(summary = "읽음 상태 목록 조회", description = "사용자 ID로 해당 사용자의 채널별 읽음 상태 목록 반환")
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> findAllReadStatuses(@RequestParam("userId")UUID userId){
        return readStatusService.findAllByUserId(userId);
    }
}