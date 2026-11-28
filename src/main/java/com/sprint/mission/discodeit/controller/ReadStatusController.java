package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.annotation.swagger.readstatus.ReadStatusCreateDocs;
import com.sprint.mission.discodeit.annotation.swagger.readstatus.ReadStatusFindByUserIdDocs;
import com.sprint.mission.discodeit.annotation.swagger.readstatus.ReadStatusUpdateDocs;
import com.sprint.mission.discodeit.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @ReadStatusCreateDocs
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> postReadStatus(
            @Valid @RequestBody ReadStatusCreateRequest request) {

        ReadStatus readStatus = readStatusService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @ReadStatusFindByUserIdDocs
    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> getAllByUserId(
            @Parameter(description = "조회할 User ID", required = true)
            @RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @ReadStatusUpdateDocs
    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ReadStatus putReadStatus(
            @Parameter(description = "수정할 읽음 상태 ID", required = true)
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request) {

        return readStatusService.update(readStatusId, request);
    }
}
