package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatus readStatus = readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @RequestMapping(
            value = "/{readStatusId}",
            method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReadStatus> update(
            @PathVariable("readStatusId") UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusUpdateRequest serviceRequest = new ReadStatusUpdateRequest(
                readStatusId,
                request.lastReadAt()
        );

        readStatusService.update(serviceRequest);

        ReadStatus readStatus = readStatusService.findById(readStatusId);

        return ResponseEntity.ok(readStatus);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(
            @RequestParam("userId") UUID userId
    ) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(readStatuses);
    }
}