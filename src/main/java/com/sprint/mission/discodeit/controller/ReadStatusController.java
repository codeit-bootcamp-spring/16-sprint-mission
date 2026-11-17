package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusListResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/readstatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponse> createReadStatus(@Valid @RequestBody ReadStatusCreateRequest request) {
        ReadStatusResponse readStatusResponse = this.readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusResponse);
    }

    @PatchMapping
    public ResponseEntity<ReadStatusResponse> updateReadStatus(@Valid @RequestBody ReadStatusUpdateRequest request) {
        ReadStatusResponse readStatusResponse = this.readStatusService.update(request);

        return ResponseEntity.ok(readStatusResponse);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<ReadStatusListResponse> findReadStatusesByUserId(@PathVariable("user-id") UUID userId) {
        List<ReadStatusResponse> readStatusListResponse = this.readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(new ReadStatusListResponse(readStatusListResponse));
    }
}

