package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStatus.*;
import com.sprint.mission.discodeit.service.basic.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/read-status")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ReadStatusFindResponse>> findAllReadStatusByUserId(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.findAllByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ReadStatusCreateResponse> createReadStatus(@RequestBody ReadStatusCreateRequest createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.readStatusService.create(createRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReadStatusUpdateResponse> updateReadStatus(@PathVariable UUID id,
                                                                     @RequestBody ReadStatusUpdateRequest updateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.readStatusService.update(id, updateRequest));
    }
}
