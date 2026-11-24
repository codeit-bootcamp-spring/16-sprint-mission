package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
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
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {
    private final ReadStatusService readStatusService;

    @Override
    public ResponseEntity<ReadStatus> createReadStatus(ReadStatusCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
    }

    @Override
    public ResponseEntity<ReadStatus> updateReadStatus(UUID readStatusId, ReadStatusUpdateRequest request) {
        return ResponseEntity.ok(readStatusService.update(readStatusId, request));
    }

    @Override
    public ResponseEntity<List<ReadStatus>> findAllByUserId(UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}